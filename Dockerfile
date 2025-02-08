FROM eclipse-temurin:21.0.1_12-jdk-alpine AS builder
# speed up Maven JVM a bit
# set working directory
WORKDIR .
# copy your other files
COPY . .
# compile the source code and package it in a jar file
RUN ./mvnw clean install -Dmaven.test.skip=true -U
RUN ./mvnw clean package -Dmaven.test.skip=true

#Stage 2
#set base image for second stage
FROM eclipse-temurin:21.0.1_12-jre-alpine AS final
WORKDIR /opt/recommendic
EXPOSE 8080
COPY --from=builder ./target/*.jar .

ENTRYPOINT ["java", "-jar","--enable-preview" ,"./recommendic-0.0.1-SNAPSHOT.jar"]