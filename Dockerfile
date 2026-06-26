FROM eclipse-temurin:25-jdk-alpine-3.23
COPY target/04_EComm-Monolith-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]