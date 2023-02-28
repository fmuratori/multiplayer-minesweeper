# syntax=docker/dockerfile:1

FROM gradle:6.9.3-jdk11-alpine

WORKDIR /app

COPY . .

RUN gradle build

# PORTS:
# - 8003: Http server
# - 8004: Socket.IO server
EXPOSE 8003
EXPOSE 8004

CMD ["gradle", "run"]

