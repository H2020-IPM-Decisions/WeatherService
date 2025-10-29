package net.ipmdecisions.weather.datasourceadapters.v2.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetosResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.MetosParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
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

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Date;

@ApplicationScoped
public class MetosClient implements Client {

    @Override
    public Object getData(ParamModel paramModel) {
        var params = (MetosParamModel) paramModel;
        try {
            String stationInfoJson = get("/station/" + params.getStationID(),
                    params.getPublicKey(), params.getPrivateKey());

            String timezoneCode = getNodeAsString(stationInfoJson, "/position/timezoneCode");
            ZoneId locationZone = ZoneId.of(timezoneCode);
            long fromEpochSeconds = params.getStartDate().atStartOfDay(locationZone).toEpochSecond();

            String dataJson = get("/data/optimized/" + params.getStationID() + "/hourly/from/" + fromEpochSeconds,
                    params.getPublicKey(), params.getPrivateKey());

            return new MetosResponse(stationInfoJson, dataJson, params);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(String api) {
        return api.equals("metos");
    }

    private String get(String path, String publicKey, String privateKey)
            throws GeneralSecurityException, IOException {

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
        String date = DateUtils.formatDate(new Date(System.currentTimeMillis()));
        String contentToSign = method + path + date + publicKey;
        String signature = hmacSHA256Hex(contentToSign, privateKey);
        String authorizationString = "hmac " + publicKey + ":" + signature;

        HttpUriRequest request = RequestBuilder.get().setUri("https://api.fieldclimate.com/v1" + path)
                .setHeader(HttpHeaders.ACCEPT, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, authorizationString)
                .setHeader(HttpHeaders.DATE, date).build();

        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() == 401) {
            throw new NotAuthorizedException("Access denied by Metos FieldClimate. Please check your credentials");
        }
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    private static String hmacSHA256Hex(String data, String key) throws GeneralSecurityException {
        try {
            SecretKeySpec secretKey =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            return Hex.encodeHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new GeneralSecurityException(e);
        }
    }

    static String getNodeAsString(String json, String pointer) {
        try {
            var om = new ObjectMapper();
            var node = om.readTree(json).at(pointer.replace('.', '/'));
            return node.isMissingNode() ? null : node.asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}