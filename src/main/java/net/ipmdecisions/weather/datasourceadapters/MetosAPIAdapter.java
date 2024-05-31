/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPM Decisions Weather Service.
 * IPM Decisions Weather Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPM Decisions Weather Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPM Decisions Weather Service.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.datasourceadapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.NotAuthorizedException;

import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import net.ipmdecisions.weather.util.vips.WeatherElements;
import net.ipmdecisions.weather.util.vips.WeatherUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Gets data from the Pessl METOS fieldclimate API.
 * Read about the API here: https://api.fieldclimate.com/v1/docs/
 * 
 * @copyright 2017 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class MetosAPIAdapter {

private static Logger LOGGER = LoggerFactory.getLogger(MetosAPIAdapter.class);

    private WeatherUtils wUtils;
    
    public MetosAPIAdapter(){
        this.wUtils = new WeatherUtils();
    }
    

    // Mappping VIPS parameters and Metos sensors
    // Fieldclimate codes should be in decreasing priority
    private final ParamInfo[] PARAM_MAP = {
        new ParamInfo(WeatherElements.PRECIPITATION,            new Integer[] {6},              "sum"),
        new ParamInfo(WeatherElements.LEAF_WETNESS_DURATION,    new Integer[] {4},              "time"),
        new ParamInfo(WeatherElements.GLOBAL_RADIATION,         new Integer[] {600},            "avg"),
        new ParamInfo(WeatherElements.WIND_SPEED_2M,            new Integer[] {5},              "avg"),
        new ParamInfo(WeatherElements.TEMPERATURE_MEAN,         new Integer[] {0,16385,506},    "avg"),
        new ParamInfo(WeatherElements.TEMPERATURE_MAXIMUM,      new Integer[] {16385,506},      "max"),
        new ParamInfo(WeatherElements.TEMPERATURE_MINIMUM,      new Integer[] {16385,506},      "min"),
        new ParamInfo(WeatherElements.RELATIVE_HUMIDITY_MEAN,   new Integer[] {1,507,21778},    "avg")
    };
    
    public List<VIPSWeatherObservation> getParsedObservations(String jsonTxt, TimeZone timeZone, Date startDate) throws IOException, ParseException
    {
        
        ObjectMapper oMapper = new ObjectMapper();
        JsonNode jNode = oMapper.readTree(jsonTxt);
        List<VIPSWeatherObservation> retVal = new ArrayList<>();
        
        JsonNode dates = jNode.get("dates");
        JsonNode data = jNode.get("data");
        if(data != null)
        {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dFormat.setTimeZone(timeZone);
            //dFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
            for(ParamInfo paramInfo:this.PARAM_MAP)
            {
                boolean foundParam = false;
                for(Integer code:paramInfo.preferredCodes)
                {
                    for(JsonNode aData:data)
                    {
                        if(aData.get("code").asInt() == code)
                        {
                            for(int i=0;i< dates.size();i++)
                            {
                                Date timeMeasured = dFormat.parse(dates.get(i).asText());
                                if(!timeMeasured.before(startDate))
                                {
                                    VIPSWeatherObservation obs = new VIPSWeatherObservation();
                                    obs.setTimeMeasured(timeMeasured);
                                    obs.setElementMeasurementTypeId(paramInfo.VIPSCode);
                                    obs.setValue(aData.get("aggr").get(paramInfo.aggregationType).get(i).asDouble());
                                    obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                                    retVal.add(obs);
                                }
                                //System.out.println(obs.toString());
                            }
                            foundParam = true;
                        }
                        if(foundParam)
                        {
                            break;
                        }
                    }
                    if(foundParam)
                    {
                        break;
                    }
                }
            }
        }
        return retVal;
    }
    
    public WeatherData getWeatherData(String stationID, String publicKey, String privateKey, LocalDate startDate, LocalDate endDate) throws GeneralSecurityException, IOException, ParseWeatherDataException
    {
        // The MetOS stations may have a GMT offset for data that differs from the location timeZone
        // For instance, our test station in Lithuania has location timeZone "Europe/Vilnius" (UTC + 2) (Standard for Lithuania, obviously),
        // while the data has 3 hours offset from UTC.
        JsonNode stationInfo = this.getStationInfo(stationID, publicKey, privateKey);
        ObjectMapper om = new ObjectMapper();
        //System.out.println(om.writeValueAsString(stationInfo));
        Integer minutesOffset = stationInfo.get("config").get("timezone_offset").asInt();
        Integer GMTOffset = minutesOffset/60;
        TimeZone dataTimeZone =  GMTOffset >= 0 ? TimeZone.getTimeZone("GMT+" + GMTOffset) : TimeZone.getTimeZone("GMT" + GMTOffset);
        String timeZoneCode = stationInfo.get("position").get("timezoneCode").asText();
        TimeZone locationTimeZone = TimeZone.getTimeZone(ZoneId.of(timeZoneCode));
        List<VIPSWeatherObservation> VIPSobs = this.getWeatherObservations(stationID, publicKey, privateKey, Date.from(startDate.atStartOfDay(ZoneId.of(locationTimeZone.getID())).toInstant()), dataTimeZone);
        List<Double> coordinate = new ArrayList<>();
        stationInfo.get("position").get("geo").get("coordinates").iterator().forEachRemaining(c->{
            coordinate.add(c.asDouble());
        });
        return this.wUtils.getWeatherDataFromVIPSWeatherObservations(VIPSobs, coordinate.get(0), coordinate.get(1), 0);
        
    }
    
    
    public List<VIPSWeatherObservation> getWeatherObservations(String stationId, String publicKey, String privateKey, Date startDate, TimeZone timeZone) throws ParseWeatherDataException
    {
        try
        {
            String path = "/data/optimized/" + stationId + "/hourly/from/" + (startDate.getTime() / 1000);


            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });

            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
            SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            HttpClient client = HttpClients.custom().setSSLSocketFactory(sslSF).build();

            String method = "GET";

            // Creating date in appropriate format (RFC 1123) that is accepted by http header; apache http client library is used
            String date = DateUtils.formatDate(new Date(System.currentTimeMillis()));

            String contentToSign = method + path + date + publicKey;

            String signature = generateHmacSHA256Signature(contentToSign, privateKey);
            String authorizationString = "hmac " + publicKey + ":" + signature;

            // Creating request by using RequestBuilder from apache http client library; headers are set in this step as well
            HttpUriRequest request = RequestBuilder.get().setUri("https://api.fieldclimate.com/v1" + path)
            //HttpUriRequest request = RequestBuilder.get().setUri(apiURL)
            .setHeader(HttpHeaders.ACCEPT, "application/json")
            .setHeader(HttpHeaders.AUTHORIZATION, authorizationString)
            .setHeader(HttpHeaders.DATE, date).build();

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            LOGGER.debug(String.valueOf(response.getStatusLine().getStatusCode()));

            if(response.getStatusLine().getStatusCode() == 401)
            {
                throw new NotAuthorizedException("Access denied by Metos FieldClimate. Please check your credentials");
            }

            //EntityUtils.consume(entity);
            String responseString = EntityUtils.toString(entity, "UTF-8");
            
            return this.getParsedObservations(responseString, timeZone, startDate); 
        }
        catch(IOException | ParseException | GeneralSecurityException ex)
        {
            throw new ParseWeatherDataException(ex.getMessage());
        }
    }
    
    // Static method that generates HmacSHA256 signiture as a hexademical string
    private String generateHmacSHA256Signature(String data, String key) throws GeneralSecurityException {
        byte[] hmacData = null;

        try
        {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            return Hex.encodeHexString(mac.doFinal(data.getBytes("UTF-8")));

        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException(e);
        }
    }
    
    private JsonNode getStationInfo(String stationId,String publicKey, String privateKey) throws IOException, GeneralSecurityException, NotAuthorizedException
    {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });

            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
            SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            HttpClient client = HttpClients.custom().setSSLSocketFactory(sslSF).build();

            String method = "GET";
            String path = "/station/" + stationId;
            //System.out.println("path=" + path);
            // Creating date in appropriate format (RFC 1123) that is accepted by http header; apache http client library is used
            String date = DateUtils.formatDate(new Date(System.currentTimeMillis()));

            String contentToSign = method + path + date + publicKey;

            String signature = generateHmacSHA256Signature(contentToSign, privateKey);
            String authorizationString = "hmac " + publicKey + ":" + signature;

            // Creating request by using RequestBuilder from apache http client library; headers are set in this step as well
            HttpUriRequest request = RequestBuilder.get().setUri("https://api.fieldclimate.com/v1" + path)
            .setHeader(HttpHeaders.ACCEPT, "application/json")
            .setHeader(HttpHeaders.AUTHORIZATION, authorizationString)
            .setHeader(HttpHeaders.DATE, date).build();

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            if(response.getStatusLine().getStatusCode() == 401)
            {
                throw new NotAuthorizedException("Access denied by Metos FieldClimate. Please check your credentials");
            }

            LOGGER.debug(String.valueOf(response.getStatusLine().getStatusCode()));

            //EntityUtils.consume(entity);
            String responseString = EntityUtils.toString(entity, "UTF-8");
            //System.out.println(responseString);
            ObjectMapper oMapper = new ObjectMapper();
            JsonNode jNode = oMapper.readTree(responseString);
            return jNode;
            
    }
    
    /**
     * Data structure for easy parameter mapping
     */
    private class ParamInfo {

        public final String VIPSCode;
        public final Integer[] preferredCodes;
        public final String aggregationType;
        public ParamInfo(String VIPSCode, Integer[] preferredCodes, String aggregationType)
        {
            this.VIPSCode = VIPSCode;
            this.preferredCodes = preferredCodes;
            this.aggregationType = aggregationType;
        }

    }
    
}