# Build stage
FROM maven:3.8.4-openjdk-8-slim AS build

WORKDIR /app
COPY pom.xml .

COPY src ./src
# Copy settings.xml file for private repository access
COPY settings.xml settings.xml

# Build the project and create the executable JAR
RUN mvn --settings settings.xml clean install -DskipTests

# Run stage
FROM openjdk:8-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-Xmx768m", "-Xms768m", "-XX:+UseG1GC", "-Duser.timezone=GMT+8", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
