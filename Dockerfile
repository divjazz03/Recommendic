### ----------------------------- Build Stage ------------------------
FROM eclipse-temurin:21.0.1_12-jdk AS build

WORKDIR /opt/recommendic

# Copy maven wrapper & pom for caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Change to executable script
RUN chmod +x mvnw
# Download and cache dependencies
RUN ./mvnw dependency:go-offline
# Copy source
COPY src src
# Build jar
RUN ./mvnw clean package -DskipTests

#---------------------------------------- JRE Slim generator ------------------------------------------------------------
FROM eclipse-temurin:21.0.1_12-jre as run

WORKDIR /opt/recommendic
COPY --from=build /opt/recommendic/target/*.jar app.jar

# Generate a minimal JRE

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}

RUN java -Xshare:dump

ENTRYPOINT ["java","-Xshare:on", "--enable-preview","-jar" ,"./app.jar" ]
