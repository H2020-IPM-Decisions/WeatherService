# the first stage of our build will use a maven 3.6 parent image
FROM maven:3.6-openjdk-11 AS MAVEN_BUILD
 
# copy the pom and src code to the container
COPY ./ ./
 
# package our application code
RUN mvn clean install

RUN git clone --single-branch --branch master https://github.com/H2020-IPM-Decisions/formats.git

# Used this as a template: https://github.com/jboss-dockerfiles/wildfly/blob/master/Dockerfile 
# Use latest jboss/base-jdk:11 image as the base
FROM jboss/base-jdk:11


# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 20.0.0.Final
ENV WILDFLY_SHA1 3cab3453c9270c662766417adf16c27806124361
ENV JBOSS_HOME /opt/jboss/wildfly

USER root

# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME \
    && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz \
    && chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/IPMDecisionsWeatherService-ALPHA-SNAPSHOT.war /IPMDecisionsWeatherService-ALPHA-SNAPSHOT.war
#COPY --from=MAVEN_BUILD /geo-countries/data/countries.geojson /countries.geojson
# This requires you to have cloned the formats repository from GitHub: https://github.com/H2020-IPM-Decisions/formats
COPY  --from=MAVEN_BUILD /formats/weather_data/Weather_data_sources.yaml /Weather_data_sources.yaml
RUN ln -s /IPMDecisionsWeatherService-ALPHA-SNAPSHOT.war ${JBOSS_HOME}/standalone/deployments/IPMDecisionsWeatherService.war

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER jboss

# Expose the ports we're interested in
EXPOSE 8080

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0","-Dnet.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE=/Weather_data_sources.yaml"]