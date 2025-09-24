# Dockerfile for Spring Boot WAR deployment
# Place this file in the root of your backend repository

# Use official Tomcat image with JDK 11
FROM tomcat:9.0-jdk11-openjdk-slim

# Remove default ROOT webapp
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your WAR file to Tomcat
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]