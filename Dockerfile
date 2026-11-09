FROM maven:3.9.9-eclipse-temurin-17 AS build
ARG SERVICE
WORKDIR /workspace
COPY pom.xml .
COPY config ./config
COPY services/${SERVICE}/pom.xml services/${SERVICE}/pom.xml
RUN mvn -B -q -pl services/${SERVICE} -am dependency:go-offline
COPY services/${SERVICE}/src services/${SERVICE}/src
RUN mvn -B -q -pl services/${SERVICE} -am package -DskipTests

FROM eclipse-temurin:17-jre-alpine
ARG SERVICE
WORKDIR /app
COPY --from=build /workspace/services/${SERVICE}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
