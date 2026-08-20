# ===========================
# Stage 1 - Build
# ===========================
FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

# ===========================
# Stage 2 - Runtime
# ===========================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/ConfigServer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7054

ENTRYPOINT ["java","-jar","app.jar"]