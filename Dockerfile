# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim


COPY /build/libs/young-0.0.1-SNAPSHOT.jar /app/userservice.jar


CMD ["java", "-jar", "/app/userservice.jar"]


