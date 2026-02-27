FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8010

ENTRYPOINT ["java", "-jar", "target/UserService-0.0.1-SNAPSHOT.jar"]