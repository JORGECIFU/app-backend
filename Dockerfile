# Etapa 1: compilar con Maven
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: runtime ligero
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copiamos solo el JAR generado
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]