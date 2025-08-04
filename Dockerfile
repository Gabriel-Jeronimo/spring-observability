# FROM maven:3.9.11-amazoncorretto-24-al2023 as build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn clean package -DskipTests

FROM amazoncorretto:21
WORKDIR /app

# RUN mvn clean compile package
COPY target/production-ready-homework-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]``