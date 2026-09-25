# Build a reproducible production image for an Oracle Cloud VM.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home smartcare
COPY --from=build /workspace/target/*.jar /app/smartcare.jar
USER smartcare
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/smartcare.jar"]
