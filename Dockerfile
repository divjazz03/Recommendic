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
FROM eclipse-temurin:21.0.1_12-jdk as jre-builder

# Generate a minimal JRE
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.sql,java.naming,java.management,java.xml,jdk.unsupported,java.security.jgss \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --compress=2 \
    --output /jre


### ------------------------------------------ Run Stage -----------------------------------------------------------------
FROM gcr.io/distroless/java-base AS run

WORKDIR /opt/recommendic
EXPOSE 8080
COPY --from=jre-builder /jre /opt/jre
COPY --from=build /opt/recommendic/target/*.jar /opt/recommendic/app.jar

ENV PATH="/opt/jre/bin:${PATH}"
ENV JAVA_HOME="/opt/jre"

ENTRYPOINT ["java", "--enable-preview","-jar" ,"./app.jar"]
