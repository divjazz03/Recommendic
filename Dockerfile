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
ENV JAVA_HOME="/opt/jre"
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
ENV CLOUDINARY_CLOUD_NAME='kdslkdmskl'
ENV CLOUDINARY_API_KEY='dsdspodsodspdos'
ENV CLOUDINARY_API_SECRET='dslkdlskdsldksdl'
EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "--enable-preview","-jar" ,"./app.jar" ]
