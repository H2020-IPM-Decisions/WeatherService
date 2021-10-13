
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.ipmdecisions.weather.datasourceadapters.dmi.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Point_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Point");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _WeatherDataSource_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherDataSource");
    private final static QName _ArrayOfWeatherDataModel_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "ArrayOfWeatherDataModel");
    private final static QName _WeatherDataParameter_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherDataParameter");
    private final static QName _ArrayOfstring_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfstring");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _ArrayOfNullableOfWeatherDataSourcevPY1Rzz9_QNAME = new QName("http://schemas.datacontract.org/2004/07/System", "ArrayOfNullableOfWeatherDataSourcev_PY1Rzz9");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _ExtendedWeatherResponse_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "ExtendedWeatherResponse");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _ArrayOfWeatherDataParameter_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "ArrayOfWeatherDataParameter");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _ArrayOfNullableOfdouble_QNAME = new QName("http://schemas.datacontract.org/2004/07/System", "ArrayOfNullableOfdouble");
    private final static QName _ArrayOfExtendedWeatherResponse_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "ArrayOfExtendedWeatherResponse");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _ArrayOfWeatherDataSource_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "ArrayOfWeatherDataSource");
    private final static QName _WeatherDataModel_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherDataModel");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _WeatherErrorModel_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherErrorModel");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _WeatherResponse_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherResponse");
    private final static QName _WeatherInterval_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherInterval");
    private final static QName _GetWeatherDataMultipointResponseGetWeatherDataMultipointResult_QNAME = new QName("http://tempuri.org/", "GetWeatherDataMultipointResult");
    private final static QName _GetWeatherDataXCoordinate_QNAME = new QName("http://tempuri.org/", "X_Coordinate");
    private final static QName _GetWeatherDataYCoordinate_QNAME = new QName("http://tempuri.org/", "Y_Coordinate");
    private final static QName _GetWeatherDataSources_QNAME = new QName("http://tempuri.org/", "sources");
    private final static QName _GetWeatherDataApplication_QNAME = new QName("http://tempuri.org/", "application");
    private final static QName _GetWeatherDataUTMZone_QNAME = new QName("http://tempuri.org/", "UTMZone");
    private final static QName _GetWeatherDataAgroId_QNAME = new QName("http://tempuri.org/", "agroId");
    private final static QName _GetWeatherDataWeatherDataParameters_QNAME = new QName("http://tempuri.org/", "weatherDataParameters");
    private final static QName _WeatherDataModelAirrhSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "AirrhSpecial");
    private final static QName _WeatherDataModelPrecNoCor_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "PrecNoCor");
    private final static QName _WeatherDataModelMaxtemp_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Maxtemp");
    private final static QName _WeatherDataModelSunrad_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Sunrad");
    private final static QName _WeatherDataModelWinddir_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Winddir");
    private final static QName _WeatherDataModelEvapo_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Evapo");
    private final static QName _WeatherDataModelWindspeedSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WindspeedSpecial");
    private final static QName _WeatherDataModelAirtemp_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Airtemp");
    private final static QName _WeatherDataModelWindspeed_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Windspeed");
    private final static QName _WeatherDataModelPrec_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Prec");
    private final static QName _WeatherDataModelDifradSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "DifradSpecial");
    private final static QName _WeatherDataModelGlorad_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Glorad");
    private final static QName _WeatherDataModelAirrh_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Airrh");
    private final static QName _WeatherDataModelGloradSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "GloradSpecial");
    private final static QName _WeatherDataModelWinddirSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WinddirSpecial");
    private final static QName _WeatherDataModelPrecSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "PrecSpecial");
    private final static QName _WeatherDataModelSoiltemp_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Soiltemp");
    private final static QName _WeatherDataModelLeafwet_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Leafwet");
    private final static QName _WeatherDataModelWeatherDataVariables_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherDataVariables");
    private final static QName _WeatherDataModelAirtempSum_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "AirtempSum");
    private final static QName _WeatherDataModelMintemp_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "Mintemp");
    private final static QName _WeatherDataModelWeatherDataSources_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherDataSources");
    private final static QName _WeatherDataModelAirtempSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "AirtempSpecial");
    private final static QName _WeatherDataModelLeafwetSpecial_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "LeafwetSpecial");
    private final static QName _WeatherResponseWeahterDataList_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeahterDataList");
    private final static QName _WeatherResponseWeatherError_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherError");
    private final static QName _GetWeatherDataResponseGetWeatherDataResult_QNAME = new QName("http://tempuri.org/", "GetWeatherDataResult");
    private final static QName _GetWeatherDataMultipointExtendedResponseGetWeatherDataMultipointExtendedResult_QNAME = new QName("http://tempuri.org/", "GetWeatherDataMultipointExtendedResult");
    private final static QName _GetWeatherDataMultipointExtendedXCoordinate_QNAME = new QName("http://tempuri.org/", "xCoordinate");
    private final static QName _GetWeatherDataMultipointExtendedYCoordinate_QNAME = new QName("http://tempuri.org/", "yCoordinate");
    private final static QName _GetWeatherDataMultipointExtendedUtmZone_QNAME = new QName("http://tempuri.org/", "utmZone");
    private final static QName _ExtendedWeatherResponseWeatherPoint_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherPoint");
    private final static QName _ExtendedWeatherResponseWeatherData_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "WeatherData");
    private final static QName _GetWeatherDataExtendedResponseGetWeatherDataExtendedResult_QNAME = new QName("http://tempuri.org/", "GetWeatherDataExtendedResult");
    private final static QName _WeatherErrorModelErrorMessages_QNAME = new QName("http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", "ErrorMessages");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.ipmdecisions.weather.datasourceadapters.dmi.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetWeatherDataExtendedResponse }
     * 
     */
    public GetWeatherDataExtendedResponse createGetWeatherDataExtendedResponse() {
        return new GetWeatherDataExtendedResponse();
    }

    /**
     * Create an instance of {@link WeatherResponse }
     * 
     */
    public WeatherResponse createWeatherResponse() {
        return new WeatherResponse();
    }

    /**
     * Create an instance of {@link GetWeatherDataMultipointResponse }
     * 
     */
    public GetWeatherDataMultipointResponse createGetWeatherDataMultipointResponse() {
        return new GetWeatherDataMultipointResponse();
    }

    /**
     * Create an instance of {@link ArrayOfExtendedWeatherResponse }
     * 
     */
    public ArrayOfExtendedWeatherResponse createArrayOfExtendedWeatherResponse() {
        return new ArrayOfExtendedWeatherResponse();
    }

    /**
     * Create an instance of {@link GetWeatherDataResponse }
     * 
     */
    public GetWeatherDataResponse createGetWeatherDataResponse() {
        return new GetWeatherDataResponse();
    }

    /**
     * Create an instance of {@link GetWeatherDataMultipoint }
     * 
     */
    public GetWeatherDataMultipoint createGetWeatherDataMultipoint() {
        return new GetWeatherDataMultipoint();
    }

    /**
     * Create an instance of {@link ArrayOfWeatherDataSource }
     * 
     */
    public ArrayOfWeatherDataSource createArrayOfWeatherDataSource() {
        return new ArrayOfWeatherDataSource();
    }

    /**
     * Create an instance of {@link ArrayOfWeatherDataParameter }
     * 
     */
    public ArrayOfWeatherDataParameter createArrayOfWeatherDataParameter() {
        return new ArrayOfWeatherDataParameter();
    }

    /**
     * Create an instance of {@link GetWeatherDataMultipointExtendedResponse }
     * 
     */
    public GetWeatherDataMultipointExtendedResponse createGetWeatherDataMultipointExtendedResponse() {
        return new GetWeatherDataMultipointExtendedResponse();
    }

    /**
     * Create an instance of {@link GetWeatherDataExtended }
     * 
     */
    public GetWeatherDataExtended createGetWeatherDataExtended() {
        return new GetWeatherDataExtended();
    }

    /**
     * Create an instance of {@link GetWeatherDataMultipointExtended }
     * 
     */
    public GetWeatherDataMultipointExtended createGetWeatherDataMultipointExtended() {
        return new GetWeatherDataMultipointExtended();
    }

    /**
     * Create an instance of {@link GetWeatherData }
     * 
     */
    public GetWeatherData createGetWeatherData() {
        return new GetWeatherData();
    }

    /**
     * Create an instance of {@link ArrayOfWeatherDataModel }
     * 
     */
    public ArrayOfWeatherDataModel createArrayOfWeatherDataModel() {
        return new ArrayOfWeatherDataModel();
    }

    /**
     * Create an instance of {@link WeatherDataModel }
     * 
     */
    public WeatherDataModel createWeatherDataModel() {
        return new WeatherDataModel();
    }

    /**
     * Create an instance of {@link ExtendedWeatherResponse }
     * 
     */
    public ExtendedWeatherResponse createExtendedWeatherResponse() {
        return new ExtendedWeatherResponse();
    }

    /**
     * Create an instance of {@link Point }
     * 
     */
    public Point createPoint() {
        return new Point();
    }

    /**
     * Create an instance of {@link WeatherErrorModel }
     * 
     */
    public WeatherErrorModel createWeatherErrorModel() {
        return new WeatherErrorModel();
    }

    /**
     * Create an instance of {@link ArrayOfNullableOfdouble }
     * 
     */
    public ArrayOfNullableOfdouble createArrayOfNullableOfdouble() {
        return new ArrayOfNullableOfdouble();
    }

    /**
     * Create an instance of {@link ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 }
     * 
     */
    public ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 createArrayOfNullableOfWeatherDataSourcevPY1Rzz9() {
        return new ArrayOfNullableOfWeatherDataSourcevPY1Rzz9();
    }

    /**
     * Create an instance of {@link ArrayOfstring }
     * 
     */
    public ArrayOfstring createArrayOfstring() {
        return new ArrayOfstring();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Point }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Point")
    public JAXBElement<Point> createPoint(Point value) {
        return new JAXBElement<Point>(_Point_QNAME, Point.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherDataSource")
    public JAXBElement<WeatherDataSource> createWeatherDataSource(WeatherDataSource value) {
        return new JAXBElement<WeatherDataSource>(_WeatherDataSource_QNAME, WeatherDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "ArrayOfWeatherDataModel")
    public JAXBElement<ArrayOfWeatherDataModel> createArrayOfWeatherDataModel(ArrayOfWeatherDataModel value) {
        return new JAXBElement<ArrayOfWeatherDataModel>(_ArrayOfWeatherDataModel_QNAME, ArrayOfWeatherDataModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherDataParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherDataParameter")
    public JAXBElement<WeatherDataParameter> createWeatherDataParameter(WeatherDataParameter value) {
        return new JAXBElement<WeatherDataParameter>(_WeatherDataParameter_QNAME, WeatherDataParameter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfstring")
    public JAXBElement<ArrayOfstring> createArrayOfstring(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ArrayOfstring_QNAME, ArrayOfstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System", name = "ArrayOfNullableOfWeatherDataSourcev_PY1Rzz9")
    public JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9> createArrayOfNullableOfWeatherDataSourcevPY1Rzz9(ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 value) {
        return new JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9>(_ArrayOfNullableOfWeatherDataSourcevPY1Rzz9_QNAME, ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtendedWeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "ExtendedWeatherResponse")
    public JAXBElement<ExtendedWeatherResponse> createExtendedWeatherResponse(ExtendedWeatherResponse value) {
        return new JAXBElement<ExtendedWeatherResponse>(_ExtendedWeatherResponse_QNAME, ExtendedWeatherResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "ArrayOfWeatherDataParameter")
    public JAXBElement<ArrayOfWeatherDataParameter> createArrayOfWeatherDataParameter(ArrayOfWeatherDataParameter value) {
        return new JAXBElement<ArrayOfWeatherDataParameter>(_ArrayOfWeatherDataParameter_QNAME, ArrayOfWeatherDataParameter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNullableOfdouble }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System", name = "ArrayOfNullableOfdouble")
    public JAXBElement<ArrayOfNullableOfdouble> createArrayOfNullableOfdouble(ArrayOfNullableOfdouble value) {
        return new JAXBElement<ArrayOfNullableOfdouble>(_ArrayOfNullableOfdouble_QNAME, ArrayOfNullableOfdouble.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfExtendedWeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "ArrayOfExtendedWeatherResponse")
    public JAXBElement<ArrayOfExtendedWeatherResponse> createArrayOfExtendedWeatherResponse(ArrayOfExtendedWeatherResponse value) {
        return new JAXBElement<ArrayOfExtendedWeatherResponse>(_ArrayOfExtendedWeatherResponse_QNAME, ArrayOfExtendedWeatherResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "ArrayOfWeatherDataSource")
    public JAXBElement<ArrayOfWeatherDataSource> createArrayOfWeatherDataSource(ArrayOfWeatherDataSource value) {
        return new JAXBElement<ArrayOfWeatherDataSource>(_ArrayOfWeatherDataSource_QNAME, ArrayOfWeatherDataSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherDataModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherDataModel")
    public JAXBElement<WeatherDataModel> createWeatherDataModel(WeatherDataModel value) {
        return new JAXBElement<WeatherDataModel>(_WeatherDataModel_QNAME, WeatherDataModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherErrorModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherErrorModel")
    public JAXBElement<WeatherErrorModel> createWeatherErrorModel(WeatherErrorModel value) {
        return new JAXBElement<WeatherErrorModel>(_WeatherErrorModel_QNAME, WeatherErrorModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherResponse")
    public JAXBElement<WeatherResponse> createWeatherResponse(WeatherResponse value) {
        return new JAXBElement<WeatherResponse>(_WeatherResponse_QNAME, WeatherResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherInterval }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherInterval")
    public JAXBElement<WeatherInterval> createWeatherInterval(WeatherInterval value) {
        return new JAXBElement<WeatherInterval>(_WeatherInterval_QNAME, WeatherInterval.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfExtendedWeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetWeatherDataMultipointResult", scope = GetWeatherDataMultipointResponse.class)
    public JAXBElement<ArrayOfExtendedWeatherResponse> createGetWeatherDataMultipointResponseGetWeatherDataMultipointResult(ArrayOfExtendedWeatherResponse value) {
        return new JAXBElement<ArrayOfExtendedWeatherResponse>(_GetWeatherDataMultipointResponseGetWeatherDataMultipointResult_QNAME, ArrayOfExtendedWeatherResponse.class, GetWeatherDataMultipointResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "X_Coordinate", scope = GetWeatherData.class)
    public JAXBElement<String> createGetWeatherDataXCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataXCoordinate_QNAME, String.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Y_Coordinate", scope = GetWeatherData.class)
    public JAXBElement<String> createGetWeatherDataYCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataYCoordinate_QNAME, String.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sources", scope = GetWeatherData.class)
    public JAXBElement<ArrayOfWeatherDataSource> createGetWeatherDataSources(ArrayOfWeatherDataSource value) {
        return new JAXBElement<ArrayOfWeatherDataSource>(_GetWeatherDataSources_QNAME, ArrayOfWeatherDataSource.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "application", scope = GetWeatherData.class)
    public JAXBElement<String> createGetWeatherDataApplication(String value) {
        return new JAXBElement<String>(_GetWeatherDataApplication_QNAME, String.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "UTMZone", scope = GetWeatherData.class)
    public JAXBElement<String> createGetWeatherDataUTMZone(String value) {
        return new JAXBElement<String>(_GetWeatherDataUTMZone_QNAME, String.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "agroId", scope = GetWeatherData.class)
    public JAXBElement<String> createGetWeatherDataAgroId(String value) {
        return new JAXBElement<String>(_GetWeatherDataAgroId_QNAME, String.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "weatherDataParameters", scope = GetWeatherData.class)
    public JAXBElement<ArrayOfWeatherDataParameter> createGetWeatherDataWeatherDataParameters(ArrayOfWeatherDataParameter value) {
        return new JAXBElement<ArrayOfWeatherDataParameter>(_GetWeatherDataWeatherDataParameters_QNAME, ArrayOfWeatherDataParameter.class, GetWeatherData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "AirrhSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelAirrhSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelAirrhSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "PrecNoCor", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelPrecNoCor(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelPrecNoCor_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Maxtemp", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelMaxtemp(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelMaxtemp_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Sunrad", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelSunrad(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelSunrad_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Winddir", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelWinddir(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelWinddir_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Evapo", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelEvapo(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelEvapo_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WindspeedSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelWindspeedSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelWindspeedSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Airtemp", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelAirtemp(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelAirtemp_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Windspeed", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelWindspeed(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelWindspeed_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Prec", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelPrec(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelPrec_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "DifradSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelDifradSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelDifradSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Glorad", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelGlorad(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelGlorad_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Airrh", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelAirrh(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelAirrh_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "GloradSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelGloradSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelGloradSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WinddirSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelWinddirSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelWinddirSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "PrecSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelPrecSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelPrecSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Soiltemp", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelSoiltemp(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelSoiltemp_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Leafwet", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelLeafwet(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelLeafwet_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNullableOfdouble }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherDataVariables", scope = WeatherDataModel.class)
    public JAXBElement<ArrayOfNullableOfdouble> createWeatherDataModelWeatherDataVariables(ArrayOfNullableOfdouble value) {
        return new JAXBElement<ArrayOfNullableOfdouble>(_WeatherDataModelWeatherDataVariables_QNAME, ArrayOfNullableOfdouble.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "AirtempSum", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelAirtempSum(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelAirtempSum_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "Mintemp", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelMintemp(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelMintemp_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherDataSources", scope = WeatherDataModel.class)
    public JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9> createWeatherDataModelWeatherDataSources(ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 value) {
        return new JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9>(_WeatherDataModelWeatherDataSources_QNAME, ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 .class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "AirtempSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelAirtempSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelAirtempSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "LeafwetSpecial", scope = WeatherDataModel.class)
    public JAXBElement<Double> createWeatherDataModelLeafwetSpecial(Double value) {
        return new JAXBElement<Double>(_WeatherDataModelLeafwetSpecial_QNAME, Double.class, WeatherDataModel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeahterDataList", scope = WeatherResponse.class)
    public JAXBElement<ArrayOfWeatherDataModel> createWeatherResponseWeahterDataList(ArrayOfWeatherDataModel value) {
        return new JAXBElement<ArrayOfWeatherDataModel>(_WeatherResponseWeahterDataList_QNAME, ArrayOfWeatherDataModel.class, WeatherResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherErrorModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherError", scope = WeatherResponse.class)
    public JAXBElement<WeatherErrorModel> createWeatherResponseWeatherError(WeatherErrorModel value) {
        return new JAXBElement<WeatherErrorModel>(_WeatherResponseWeatherError_QNAME, WeatherErrorModel.class, WeatherResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "X_Coordinate", scope = GetWeatherDataExtended.class)
    public JAXBElement<String> createGetWeatherDataExtendedXCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataXCoordinate_QNAME, String.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Y_Coordinate", scope = GetWeatherDataExtended.class)
    public JAXBElement<String> createGetWeatherDataExtendedYCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataYCoordinate_QNAME, String.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sources", scope = GetWeatherDataExtended.class)
    public JAXBElement<ArrayOfWeatherDataSource> createGetWeatherDataExtendedSources(ArrayOfWeatherDataSource value) {
        return new JAXBElement<ArrayOfWeatherDataSource>(_GetWeatherDataSources_QNAME, ArrayOfWeatherDataSource.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "application", scope = GetWeatherDataExtended.class)
    public JAXBElement<String> createGetWeatherDataExtendedApplication(String value) {
        return new JAXBElement<String>(_GetWeatherDataApplication_QNAME, String.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "UTMZone", scope = GetWeatherDataExtended.class)
    public JAXBElement<String> createGetWeatherDataExtendedUTMZone(String value) {
        return new JAXBElement<String>(_GetWeatherDataUTMZone_QNAME, String.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "agroId", scope = GetWeatherDataExtended.class)
    public JAXBElement<String> createGetWeatherDataExtendedAgroId(String value) {
        return new JAXBElement<String>(_GetWeatherDataAgroId_QNAME, String.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "weatherDataParameters", scope = GetWeatherDataExtended.class)
    public JAXBElement<ArrayOfWeatherDataParameter> createGetWeatherDataExtendedWeatherDataParameters(ArrayOfWeatherDataParameter value) {
        return new JAXBElement<ArrayOfWeatherDataParameter>(_GetWeatherDataWeatherDataParameters_QNAME, ArrayOfWeatherDataParameter.class, GetWeatherDataExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetWeatherDataResult", scope = GetWeatherDataResponse.class)
    public JAXBElement<WeatherResponse> createGetWeatherDataResponseGetWeatherDataResult(WeatherResponse value) {
        return new JAXBElement<WeatherResponse>(_GetWeatherDataResponseGetWeatherDataResult_QNAME, WeatherResponse.class, GetWeatherDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfExtendedWeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetWeatherDataMultipointExtendedResult", scope = GetWeatherDataMultipointExtendedResponse.class)
    public JAXBElement<ArrayOfExtendedWeatherResponse> createGetWeatherDataMultipointExtendedResponseGetWeatherDataMultipointExtendedResult(ArrayOfExtendedWeatherResponse value) {
        return new JAXBElement<ArrayOfExtendedWeatherResponse>(_GetWeatherDataMultipointExtendedResponseGetWeatherDataMultipointExtendedResult_QNAME, ArrayOfExtendedWeatherResponse.class, GetWeatherDataMultipointExtendedResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "xCoordinate", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<String> createGetWeatherDataMultipointExtendedXCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataMultipointExtendedXCoordinate_QNAME, String.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "yCoordinate", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<String> createGetWeatherDataMultipointExtendedYCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataMultipointExtendedYCoordinate_QNAME, String.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sources", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<ArrayOfWeatherDataSource> createGetWeatherDataMultipointExtendedSources(ArrayOfWeatherDataSource value) {
        return new JAXBElement<ArrayOfWeatherDataSource>(_GetWeatherDataSources_QNAME, ArrayOfWeatherDataSource.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "application", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<String> createGetWeatherDataMultipointExtendedApplication(String value) {
        return new JAXBElement<String>(_GetWeatherDataApplication_QNAME, String.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "agroId", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<String> createGetWeatherDataMultipointExtendedAgroId(String value) {
        return new JAXBElement<String>(_GetWeatherDataAgroId_QNAME, String.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "weatherDataParameters", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<ArrayOfWeatherDataParameter> createGetWeatherDataMultipointExtendedWeatherDataParameters(ArrayOfWeatherDataParameter value) {
        return new JAXBElement<ArrayOfWeatherDataParameter>(_GetWeatherDataWeatherDataParameters_QNAME, ArrayOfWeatherDataParameter.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "utmZone", scope = GetWeatherDataMultipointExtended.class)
    public JAXBElement<String> createGetWeatherDataMultipointExtendedUtmZone(String value) {
        return new JAXBElement<String>(_GetWeatherDataMultipointExtendedUtmZone_QNAME, String.class, GetWeatherDataMultipointExtended.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Point }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherPoint", scope = ExtendedWeatherResponse.class)
    public JAXBElement<Point> createExtendedWeatherResponseWeatherPoint(Point value) {
        return new JAXBElement<Point>(_ExtendedWeatherResponseWeatherPoint_QNAME, Point.class, ExtendedWeatherResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "WeatherData", scope = ExtendedWeatherResponse.class)
    public JAXBElement<WeatherResponse> createExtendedWeatherResponseWeatherData(WeatherResponse value) {
        return new JAXBElement<WeatherResponse>(_ExtendedWeatherResponseWeatherData_QNAME, WeatherResponse.class, ExtendedWeatherResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "xCoordinate", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<String> createGetWeatherDataMultipointXCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataMultipointExtendedXCoordinate_QNAME, String.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "yCoordinate", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<String> createGetWeatherDataMultipointYCoordinate(String value) {
        return new JAXBElement<String>(_GetWeatherDataMultipointExtendedYCoordinate_QNAME, String.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sources", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<ArrayOfWeatherDataSource> createGetWeatherDataMultipointSources(ArrayOfWeatherDataSource value) {
        return new JAXBElement<ArrayOfWeatherDataSource>(_GetWeatherDataSources_QNAME, ArrayOfWeatherDataSource.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "application", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<String> createGetWeatherDataMultipointApplication(String value) {
        return new JAXBElement<String>(_GetWeatherDataApplication_QNAME, String.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "agroId", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<String> createGetWeatherDataMultipointAgroId(String value) {
        return new JAXBElement<String>(_GetWeatherDataAgroId_QNAME, String.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "weatherDataParameters", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<ArrayOfWeatherDataParameter> createGetWeatherDataMultipointWeatherDataParameters(ArrayOfWeatherDataParameter value) {
        return new JAXBElement<ArrayOfWeatherDataParameter>(_GetWeatherDataWeatherDataParameters_QNAME, ArrayOfWeatherDataParameter.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "utmZone", scope = GetWeatherDataMultipoint.class)
    public JAXBElement<String> createGetWeatherDataMultipointUtmZone(String value) {
        return new JAXBElement<String>(_GetWeatherDataMultipointExtendedUtmZone_QNAME, String.class, GetWeatherDataMultipoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeatherResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetWeatherDataExtendedResult", scope = GetWeatherDataExtendedResponse.class)
    public JAXBElement<WeatherResponse> createGetWeatherDataExtendedResponseGetWeatherDataExtendedResult(WeatherResponse value) {
        return new JAXBElement<WeatherResponse>(_GetWeatherDataExtendedResponseGetWeatherDataExtendedResult_QNAME, WeatherResponse.class, GetWeatherDataExtendedResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", name = "ErrorMessages", scope = WeatherErrorModel.class)
    public JAXBElement<ArrayOfstring> createWeatherErrorModelErrorMessages(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_WeatherErrorModelErrorMessages_QNAME, ArrayOfstring.class, WeatherErrorModel.class, value);
    }

}
