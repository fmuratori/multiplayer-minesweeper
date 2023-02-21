# syntax=docker/dockerfile:1

FROM gradle:6.9.3-jdk11-alpine

WORKDIR /app
COPY . .

RUN gradle build


# PORTS:
# - 8001: Http server
# - 8002: Socket.IO server
EXPOSE 8001
EXPOSE 8002

CMD ["gradle", "run"]
