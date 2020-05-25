<img src="https://ipmdecisions.net/media/phdj3qhd/output-onlinepngtools.png" width="200px;"/>

# IPM Decisions Weather Service
This service is part of the <a href="https://ipmdecisions.net/" target="new">Horizon 2020 IPM Decisions project</a>.
The service provides the system with sufficient information for a client to be able to connect to and get information from any weather data source. 

The source code for this service can be found here: <a href="https://github.com/H2020-IPM-Decisions/WeatherService" target="new">https://github.com/H2020-IPM-Decisions/WeatherService</a>

There are three main components of the service:

## 1. The platform's standard weather data format
The format is described as a <a href="https://json-schema.org/" target="new">Json schema</a>, you can find it 
<ul>
<li><a href="../rest/schema/weatherdata" target="new">here</a> if you are reading this from a running version of the site</li>
<li><a href="https://ipmdecisions.nibio.no/weather/rest/schema/weatherdata" target="new">here</a> if you are looking directly at README.md in the source code. (It may not be the most recent one)</li>
</ul>
The schema and the validation service for weather data are part of the MetaDataService.

## 2. A catalogue of weather data sources available to the platform
The catalogue of weather data sources is a searchable list of weather data providers available to the platform. Each data source is described both in human readable format and through meta data. The latter enables a client to generate a weather data request to send to the data source. The catalogue is available from the WeatherDataSourceService.

## 3. Adapters for weather data sources to get weather data in the standard format
Some weather data sources may agree to deliver their weather data in the platform’s format directly. For the data sources that do not, adapters have to be programmed. The adapter's role is to download the data from the specified source and transform it into the platform's format. If the platform is using an adapter to download the weather data from a data source, the adapter's endpoint is specified in the weather data source catalogue.
The adapters are available from the WeatherAdapterService.

If you are reading this on GitHub or locally in your repository, the documentation is available in the source code. If you are reading this from a running site, you will find all of the services and data types described in detail in the current web site, which is auto generated using <a href="https://enunciate.webcohesion.com/" target="new">Enunciate</a>

