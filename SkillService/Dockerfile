# Stage 1: Build 
# Using Maven 3.9+ and Java 21 for Spring Boot 4 compatibility
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
# Using Eclipse Temurin JRE 21 (Lightweight)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8095
ENTRYPOINT ["java", "-jar", "app.jar"]