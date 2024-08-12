FROM eclipse-temurin:21.0.1_12-jdk-alpine AS builder
# speed up Maven JVM a bit
# set working directory
WORKDIR /opt/recommendic
# copy just mvn
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# go-offline using the pom.xml
RUN ./mvnw dependency:go-offline
# copy your other files
COPY src ./src
# compile the source code and package it in a jar file
RUN ./mvnw package -Dmaven.test.skip=true
#Stage 2
#set base image for second stage
FROM eclipse-temurin:21.0.1_12-jre-alpine AS final
WORKDIR /opt/recommendic
EXPOSE 8080
COPY --from=builder /opt/recommendic/target/*.jar /opt/recommendic/*.jar

ENTRYPOINT ["java", "-jar","--enable-preview" ,"/opt/recommendic/*.jar"]