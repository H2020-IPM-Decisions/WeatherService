# the first stage of our build will use a maven 3.6 parent image
FROM maven:3.6-openjdk-11 AS MAVEN_BUILD
 
# copy the pom and src code to the container
COPY ./ ./
 
# package our application code
RUN mvn clean install

RUN git clone --single-branch --branch main https://gitlab.nibio.no/madiphs/weather-metadata.git Weather-Metadata
RUN git clone --single-branch --branch master https://github.com/datasets/geo-countries.git

# Used this as a template: https://github.com/jboss-dockerfiles/wildfly/blob/master/Dockerfile 
# Use latest jboss/base-jdk:11 image as the base
FROM jboss/base-jdk:11


# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 25.0.0.Final
ENV WILDFLY_SHA1 238e67f48f1bd1e79f2d845cba9194dcd54b4d89
ENV JBOSS_HOME /opt/jboss/wildfly

USER root

# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME \
    && curl -O curl -O -L https://github.com/wildfly/wildfly/releases/download/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz \
    && chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

# Replace standalone.xml (the main WildFly config file)
COPY ./wildfly_config/standalone.xml_${WILDFLY_VERSION} ${JBOSS_HOME}/standalone/configuration/standalone.xml  

ENV APP_VERSION=1.0.0

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/IPMDecisionsWeatherService-$APP_VERSION.war /IPMDecisionsWeatherService-$APP_VERSION.war
COPY --from=MAVEN_BUILD /geo-countries/data/countries.geojson /countries.geojson
RUN ln -s /IPMDecisionsWeatherService-$APP_VERSION.war ${JBOSS_HOME}/standalone/deployments/IPMDecisionsWeatherService.war
# This requires you to have cloned the formats repository from GitHub: https://github.com/H2020-IPM-Decisions/dss-metadata
RUN mkdir /Weather-Metadata
COPY  --from=MAVEN_BUILD /Weather-Metadata/ /Weather-Metadata/
RUN chown -R jboss:jboss /Weather-Metadata


# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER jboss

# Expose the ports we're interested in
EXPOSE 8080

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interfaces
CMD /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Dnet.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE=/Weather-Metadata/weather_datasources.yaml -Dnet.ipmdecisions.weatherservice.COUNTRY_BOUNDARIES_FILE=/countries.geojson -Dnet.ipmdecisions.weatherservice.WEATHER_API_URL=${WEATHER_API_URL} -Dnet.ipmdecisions.weatherservice.BEARER_TOKEN_fr.meteo-concept.api=${BEARER_TOKEN_fr_meteo-concept_api}
