# ---- build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Leverage layer caching
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---- runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# Non-root user (security 101)
RUN useradd -r -u 1001 appuser
USER appuser

# Copy fat jar from build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Env-driven config (matches your application.properties)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
