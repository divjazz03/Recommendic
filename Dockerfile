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
COPY --from=jre-builder /jre /opt/jre
COPY --from=build /opt/recommendic/target/*.jar /opt/recommendic/app.jar

ENV PATH="/opt/jre/bin:${PATH}"
ENV JAVA_HOME="/opt/jre"
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV POSTGRES_DB=recommendic
ENV POSTGRES_HOST=localhost
ENV POSTGRES_PORT=5432
ENV DATABASE_PASSWORD=june12003
ENV DATABASE_USER=divjazz
ENV DATABASE_URL='jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}'
ENV EMAIL_HOST=smtp.gmail.com
ENV EMAIL_PORT=465
ENV EMAIL_ID=fallBack@gmail.com
ENV EMAIL_PASSWORD=jdsdsddlksl;lkdsdj
ENV VERIFY_EMAIL_HOST=localhost:8080
ENV REDIS_DATABASE_URL='redis://localhost:6379'
ENV CLOUDINARY_CLOUD_NAME='kdslkdmskl'
ENV CLOUDINARY_API_KEY='dsdspodsodspdos'
ENV CLOUDINARY_API_SECRET='dslkdlskdsldksdl'
EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "$JAVA_OPTS", "--enable-preview","-jar","--spring.data.redis.url=${REDIS_DATABASE_URL}" ,"./app.jar"]
