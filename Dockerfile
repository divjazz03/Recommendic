FROM eclipse-temurin:21.0.1_12-jdk-alpine AS builder
# speed up Maven JVM a bit
# set working directory
WORKDIR .
# copy your other files
COPY . .
# compile the source code and package it in a jar file
RUN ./mvnw clean package -Dmaven.test.skip=true

#Stage 2
#set base image for second stage
FROM eclipse-temurin:21.0.1_12-jre-alpine AS final
WORKDIR /opt/recommendic
EXPOSE 8080
COPY --from=builder ./target/*.jar .

ENV EMAIL_HOST=${EMAIL_HOST}
ENV DATABASE_PASSWORD=${DATABASE_USER}
ENV DATABASE_USER=${DATABASE_USER}
ENV DATABASE_URL=${DATABASE_URL}
ENV JWT_SECRET=${JWT_SECRET}
ENV EMAIL_PORT=${EMAIL_PORT}
ENV EMAIL_ID=${EMAIL_ID}
ENV EMAIL_PASSWORD=${EMAIL_PASSWORD}
ENV VERIFY_EMAIL_HOST=${VERIFY_EMAIL_HOST}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}
ENV FILE_UPLOAD_IMPL=${FILE_UPLOAD_IMPL}
ENV OPEN_FDA_API_KEY=${OPEN_FDA_API_KEY}
ENV OPEN_FDA_API_BASE_URL=${OPEN_FDA_API_BASE_URL}
ENV ADVERSE_EFFECT_END_POINT="/drug/event.json?api_key=${OPEN_FDA_API_KEY}&"
ENV DRUGS_END_POINT="/drug/drugsfda.json?api_key=${OPEN_FDA_API_KEY}&"

ENTRYPOINT ["java", "-jar","--enable-preview" ,"./recommendic.jar"]