FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN ./mvnw install:install-file -Dfile=src/main/resources/static/ClassFactoryServer-0.0.1-SNAPSHOT.jar -DgroupId=com.ecom -DartifactId=class-factory -Dversion=1.0.0 -Dpackaging=jar

RUN ./mvnw clean package -DskipTests

EXPOSE 8010

ENTRYPOINT ["java", "-jar", "target/UserService-0.0.1-SNAPSHOT.jar"]