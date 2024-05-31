# the first stage of our build will use a maven 3.6 parent image
FROM maven:3.8-openjdk-17 AS MAVEN_BUILD
 
# copy the pom and src code to the container
COPY ./ ./
 
# package our application code
RUN mvn clean install

RUN git clone --single-branch --branch master https://github.com/datasets/geo-countries.git

# Used this as a template: https://github.com/jboss-dockerfiles/wildfly/blob/master/Dockerfile 
# Use latest jboss/base-jdk:11 image as the base
FROM eclipse-temurin:17-jammy


RUN groupadd -r jboss -g 1000 && useradd -u 1000 -r -g jboss -m -d /opt/jboss -s /sbin/nologin -c "JBoss user" jboss && \
    chmod 755 /opt/jboss

# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 26.1.3.Final
ENV WILDFLY_SHA1 b9f52ba41df890e09bb141d72947d2510caf758c
ENV JBOSS_HOME /opt/jboss/wildfly

USER root

# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME \
    && curl -O -L https://github.com/wildfly/wildfly/releases/download/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz \
    && chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

# Replace standalone.xml (the main WildFly config file)
COPY ./wildfly_config/standalone.xml_${WILDFLY_VERSION} ${JBOSS_HOME}/standalone/configuration/standalone.xml  

ENV APP_VERSION=1.1.1

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/IPMDecisionsWeatherService-$APP_VERSION.war /IPMDecisionsWeatherService-$APP_VERSION.war
COPY --from=MAVEN_BUILD /geo-countries/data/countries.geojson /countries.geojson
RUN ln -s /IPMDecisionsWeatherService-$APP_VERSION.war ${JBOSS_HOME}/standalone/deployments/IPMDecisionsWeatherService.war

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER jboss

# Increase RAM for WildFly
ENV JBOSS_JAVA_SIZING="-Xms256m -Xmx8192m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=4096M"

# Expose the ports we're interested in
EXPOSE 8080

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interfaces
CMD /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Dnet.ipmdecisions.weatherservice.COUNTRY_BOUNDARIES_FILE=/countries.geojson -Dnet.ipmdecisions.weatherservice.WEATHER_API_URL=${WEATHER_API_URL} -Dnet.ipmdecisions.weatherservice.BEARER_TOKEN_fr.meteo-concept.api=${BEARER_TOKEN_fr_meteo-concept_api} -Dnet.ipmdecisions.weatherservice.SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING=${SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING}
