# Use an official Maven image as the base image
FROM maven:3.8-eclipse-temurin-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
COPY default-settings.xml ./settings.xml

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

ENV GITHUB_USERNAME $GITHUB_USERNAME
ENV GITHUB_TOKEN $GITHUB_TOKEN

# Build the application using Maven
RUN mvn package -s settings.xml -DskipTests

# Use an official OpenJDK image as the base image
FROM eclipse-temurin:21-jre

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/drone-*-jar-with-dependencies.jar app.jar

# Set the command to run the application
CMD ["java", "-jar", "app.jar"]
