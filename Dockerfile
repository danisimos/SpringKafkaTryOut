FROM openjdk:17-alpine

COPY target/spring-kafka-try-out-0.1.jar /app.jar

CMD ["java", "-jar", "/app.jar"]

