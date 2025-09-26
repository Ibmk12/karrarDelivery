# ---- Stage 1: Build ----
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set workdir inside container
WORKDIR /app

# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
COPY src ./src

# Package application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jdk-alpine

# Set workdir
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/Karrar-Delivery-0.0.1-SNAPSHOT.jar app.jar

# Expose application port (default Spring Boot is 8080)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]