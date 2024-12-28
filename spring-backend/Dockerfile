# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the fat JAR file into the container
COPY build/libs/spring-backend.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
