FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B -q -DskipTests dependency:go-offline --no-transfer-progress

COPY src ./src

RUN mvn -B -DskipTests clean package --no-transfer-progress

FROM eclipse-temurin:21-jre-jammy

RUN useradd -r -u 10001 appuser

WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar"]
CMD []