FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "target/DiscoveryService-0.0.1-SNAPSHOT.jar"]