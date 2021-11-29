# How to integrate weather data source with the platform

If you have a network of weather stations or a service that can provide online weather data from longitude/latitude, making these data available to the platform requires this:

## Implement a web service that follows the IPM Decisions protocols and standards
### Weather data format
The platform uses a custom format for weather data. It's Json formatted, and it's specified in this schema: [https://github.com/H2020-IPM-Decisions/WeatherService/blob/develop/src/main/resources/weatherDataSchema.json](https://github.com/H2020-IPM-Decisions/WeatherService/blob/develop/src/main/resources/weatherDataSchema.json)

A very simple example is given below. We get the output below from this request: [https://lmt.nibio.no/services/rest/ipmdecisions/getdata/?weatherStationId=5&parameters=1002,2001,3002,3101&interval=3600&timeStart=2020-05-01T00:00:00%2B02:00&timeEnd=2020-05-02T00:00:00%2B02:00](https://lmt.nibio.no/services/rest/ipmdecisions/getdata/?weatherStationId=5&parameters=1002,2001,3002,3101&interval=3600&timeStart=2020-05-01T00:00:00%2B02:00&timeEnd=2020-05-02T00:00:00%2B02:00) 

``` json
{
  "timeStart": "2020-04-30T22:00:00Z",
  "timeEnd": "2020-05-01T22:00:00Z",
  "interval": 3600,
  "weatherParameters": [
    1002,
    2001,
    3002,
    3101
  ],
  "locationWeatherData": [
    {
      "longitude": 10.781989,
      "latitude": 59.660468,
      "altitude": 94,
      "data": [
        [
          1.659,
          0.2,
          90.5,
          60
        ],
        [
          1.16,
          0.4,
          91.5,
          60
        ],
        [
          0.12,
          0.2,
          92.4,
          60
        ],
        [
          0.478,
          0.2,
          93.2,
          60
        ],
        [
          1.262,
          0.2,
          93.1,
          60
        ],
        [
          2.253,
          0.2,
          93.7,
          60
        ],
        [
          3.375,
          0.2,
          94.2,
          60
        ],
        [
          4.333,
          0.2,
          93.8,
          60
        ],
        [
          5.01,
          0.2,
          93.5,
          60
        ],
        [
          5.179,
          0,
          93.2,
          60
        ],
        [
          4.831,
          0.2,
          92.7,
          60
        ],
        [
          5.261,
          0,
          91.9,
          60
        ],
        [
          7.33,
          0.2,
          84.5,
          17
        ],
        [
          9.64,
          0,
          70.7,
          0
        ],
        [
          11.61,
          0.2,
          62.2,
          0
        ],
        [
          11.16,
          0.4,
          62.6,
          0
        ],
        [
          10.24,
          0.2,
          65.6,
          0
        ],
        [
          10.58,
          0.2,
          62.03,
          0
        ],
        [
          9.67,
          0.2,
          68.3,
          0
        ],
        [
          9.25,
          0.2,
          65.69,
          0
        ],
        [
          8.19,
          0,
          68.93,
          0
        ],
        [
          5.819,
          0,
          79.9,
          0
        ],
        [
          4.319,
          0.2,
          87.1,
          0
        ],
        [
          3.813,
          0,
          90.6,
          0
        ],
        [
          3.105,
          0.2,
          93.4,
          0
        ]
	  ],
	  "qc": [
		  1,
		  1,
		  1,
		  1
	  ],
      "length": 25,
      "width": 4
    }
  ]
}

```

The data should be quite easy to understand. 
* **timeStart** and **timeEnd** specifies the period of the weather data
* **interval** is given in seconds. So this is hourly data
* **weatherParameters** refers to [this list](../src/main/resources/weather_parameters_draft_v2.yaml)
* **locationWeatherData** An array of locations with weather data. This allows for 1 -> many different locations, including gridded data to be provided
* **data**: The values follow the ordering of **weatherParameters**, and are of course sorted chronologically
* **qc** refers to any quality controls having been applied to each parameter in each location. Refer to [this list](../src/main/resources/qc_tests.yaml). They're bitmapped values, so a value of 24 means code 8 + 16, meaning it failed on two tests. 

### Web service endpoint
Here's an example of a web service definition made with RestEasy/JAX-WS

``` java
@GET
    @Path("ipmdecisions/getdata")
    @Produces("application/json;charset=UTF-8")
    public Response getMeasuredDataForIPMDecisions(
            @QueryParam("weatherStationId") Integer weatherStationId,
            @QueryParam("timeStart") String timeStartStr,
            @QueryParam("timeEnd") String timeEndStr,
            @QueryParam("interval") Integer logInterval,
            @QueryParam("parameters") String ipmDecisionsParameterStr,
            @QueryParam("ignoreErrors") String ignoreErrorsStr
    ) {
      /** Internal application logic here */
    }
```

## Register your service in our database
Making the service available in the platform is done by providing IPM Decisions with the required metadata. You can see an example of the metadata below.

``` json
{
    "id": "no.met.lmt",
    "name": "Landbruksmeteorologisk tjeneste",
    "description": "Weather station network covering major agricultural areas of Norway. Data before 2010 are available by request. Email lmt@nibio.no",
    "public_URL": "https://lmt.nibio.no/",
    "endpoint": "https://lmt.nibio.no/services/rest/ipmdecisions/getdata/",
    "authentication_type": "NONE",
    "needs_data_control": "true",
    "access_type": "stations",
    "temporal": {
      "forecast": 0,
      "historic": {
        "start": "2010-01-01",
        "end": null
      }
    },
    "parameters": {
      "common": [
        1002,
        1003,
        1004,
        3002,
        2001,
        4003
      ],
      "optional": [
        3101,
        5001
      ]
    },
    "spatial": {
      "countries": [
        "NOR"
      ],
      "geoJSON": "{\n  \"type\": \"FeatureCollection\",\n  \"features\": [\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [8.68956,62.98474,5]}, \"properties\": {\"name\": \"Surnadal\", \"id\":\"46\",\"WMOCertified\": 5}},\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [5.60533332824707,59.0185012817383]}, \"properties\": {\"name\": \"Rygg\", \"id\":\"98\"}},\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [18.24073,68.6404,215]}, \"properties\": {\"name\": \"Bones\", \"id\":\"201\"}},\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [10.55906,60.35575,245]}, \"properties\": {\"name\": \"Gran\", \"id\":\"20\"}},\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [12.0244,60.54933,155]}, \"properties\": {\"name\": \"Åsnes\", \"id\":\"72\",\"WMOCertified\": null}},\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [5.04428,61.29272,12]}, \"properties\": {\"name\": \"Fureneset\", \"id\":\"16\"}},\n    {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [9.85057,59.6879,80]}, \"properties\": {\"name\": \"Darbu\", \"id\":\"86\"}},\n ]\n}"
    },
    "organization": {
      "name": "NIBIO",
      "country": "Norway",
      "address": "Postboks 115",
      "postal_code": "1431",
      "city": "Ås",
      "email": "berit.nordskog@nibio.no",
      "url": "https://www.nibio.no/"
    }
  }

```
