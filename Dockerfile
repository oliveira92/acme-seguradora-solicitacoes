# Etapa de build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY domain domain
COPY application application
COPY infrastructure infrastructure
COPY interfaces interfaces

RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Etapa de execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/interfaces/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
