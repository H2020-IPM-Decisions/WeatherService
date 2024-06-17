# Developer guide

## Building and deploying with Docker

To see your current images, run `sudo docker images`

### Build the image

The Dockerfile inside the repo root folder is the build description. To build it, run e.g.:

``` bash
sudo docker build --tag ipmdecisions/weather_api:ALPHA-04 .
```

### Run/test the image
To run it locally (assuming that you've set up your web server locally with ipmdlocaldocker as hostname):

``` bash
sudo docker run --publish 18081:8080 --detach -e WEATHER_API_URL=http://localhost:8080/WeatherService -e BEARER_TOKEN_fr_meteo-concept_api=***YOUR AUTHTOKEN HERE*** -e 'SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING=***YOUR CREDENTIALS PARAMSTRING HERE***' -e 'LWD_LSTM_HOSTNAME=***HOSTNAME, E.G. http://localhost:5000***' --name ipmweather ipmdecisions/weather_api:***YOUR VERSION***
```

If you skip the `WEATHER_API_URL` config parameter, it will be set to the default (`https://platform.ipmdecisions.net`)
If you skip the `BEARER_TOKEN_fr.meteo-concept.api` calls to the MeteoConcept weather station network in France will fail
If you skip the `SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING` calls to the Swedish LantMet weatherstation network will fail
If you skip the 'LWD_LSTM_HOSTNAME', Leaf wetness will not be calculated using the LSTM method.

Test it with Postman (url = [http://localhost:18081/WeatherService](http://localhost:18081/WeatherService)). If the tests run OK, then you can proceed to push the image. If not, you need to rebuild the image:

1. First, you need to stop the running container

```
sudo docker stop ipmweather
```

2. Then, remove the container. First find the container id

```
sudo docker ps -a -q --filter ancestor=ipmdecisions/weather_api:ALPHA-04
```
3. Then, remove it

```
sudo docker rm [CONTAINER_ID]
```

4. Then, remove the image

```
sudo docker rmi ipmdecisions/weather_api:ALPHA-04
```

5. Also, make sure you remove any ancestors as well, use sudo docker images to reveal them (check for recent ones)
6. Then you can rebuild the image (see above). Consider adding the `--no-cache` tag if you need a complete rebuild

### Login to the container’s console (e.g. for troubleshooting)
```
Sudo docker exec -it <containername> bash
```

### Push the image
```
sudo docker push ipmdecisions/weather_api:ALPHA-04
```

## Weather adapters
### DMI Point weather service
Classes have been auto generated using this command:

```
wsimport -keep -Xnocompile -p net.ipmdecisions.weather.datasourceadapters.dmi.generated  https://dmiweatherservice-plant.dlbr.dk/DMIWeatherService.svc?wsdl
```