FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "target/ApiGateway-0.0.1-SNAPSHOT.jar"]