FROM eclipse-temurin:21.0.1_12-jre-alpine AS final
WORKDIR /opt/recommendic
EXPOSE 8080
COPY ./target/*.jar .

ENTRYPOINT ["java", "-jar","--enable-preview" ,"./recommendic.jar"]