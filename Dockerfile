FROM maven:3.8-openjdk-17 AS maven_build
COPY ./ ./
RUN mvn clean install
RUN git clone --single-branch --branch main https://github.com/datasets/geo-countries.git

FROM eclipse-temurin:17-jammy

RUN groupadd -r jboss -g 1000 && useradd -u 1000 -r -g jboss -m -d /opt/jboss -s /sbin/nologin -c "App user" jboss && \
    chmod 755 /opt/jboss

USER root

ENV APP_VERSION=1.1.1

RUN mkdir -p /opt/app
COPY --from=maven_build /target/quarkus-app/lib/ /opt/app/lib/
COPY --from=maven_build /target/quarkus-app/*.jar /opt/app/
COPY --from=maven_build /target/quarkus-app/app/ /opt/app/app/
COPY --from=maven_build /target/quarkus-app/quarkus/ /opt/app/quarkus/

COPY --from=maven_build /target/IPMDecisionsWeatherService-$APP_VERSION.jar /IPMDecisionsWeatherService-$APP_VERSION.jar
COPY --from=maven_build /geo-countries/data/countries.geojson /countries.geojson

ENV LAUNCH_JBOSS_IN_BACKGROUND=true

USER jboss

ENV JBOSS_JAVA_SIZING="-Xms256m -Xmx8192m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=4096M"

EXPOSE 8080

CMD ["sh", "-c", "exec java $JBOSS_JAVA_SIZING -Dnet.ipmdecisions.weatherservice.COUNTRY_BOUNDARIES_FILE=/countries.geojson -Dnet.ipmdecisions.weatherservice.WEATHER_API_URL=${WEATHER_API_URL} -Dnet.ipmdecisions.weatherservice.BEARER_TOKEN_fr.meteo-concept.api=${BEARER_TOKEN_fr_meteo-concept_api} -Dnet.ipmdecisions.weatherservice.SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING=${SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING} -Dnet.ipmdecisions.weatherservice.LWD_LSTM_HOSTNAME=${LWD_LSTM_HOSTNAME} -jar /opt/app/quarkus-run.jar"]
