# syntax=docker/dockerfile:1

FROM gradle:6.9.3-jdk11-alpine

WORKDIR /app
COPY . .

RUN gradle build
CMD ["gradle", "run"]
EXPOSE 8001


# FROM eclipse-temurin:11
# WORKDIR /app
# COPY . .
# RUN ./gradlew build
# CMD ["gradlew", "run"]
# EXPOSE 8001