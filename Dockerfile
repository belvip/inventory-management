FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/inventory-management-*.jar app.jar

EXPOSE 8282

ENTRYPOINT ["java", "-jar", "app.jar"]