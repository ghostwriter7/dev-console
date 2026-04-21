FROM amazoncorretto:26-alpine3.21-full

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 11111

ENTRYPOINT ["java", "-jar", "app.jar"]