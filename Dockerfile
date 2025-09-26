# ==============================
# Stage 1: Build the WAR using Maven
# ==============================
FROM maven:3.8.5-openjdk-11 AS builder

# Set working directory
WORKDIR /app

# Copy pom and source code
COPY pom.xml .
COPY src ./src

# Build the WAR file (skip tests for faster build)
RUN mvn clean package -DskipTests

# ==============================
# Stage 2: Run the WAR on Tomcat
# ==============================
FROM tomcat:9.0-jdk11-openjdk-slim

# Remove default ROOT webapp
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR from builder stage
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]