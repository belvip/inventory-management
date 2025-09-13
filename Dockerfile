# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:resolve

COPY src ./src
RUN ./mvnw package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8282
ENTRYPOINT ["java", "-jar", "app.jar"]