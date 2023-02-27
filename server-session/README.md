# Multiplayer Minesweeper - Session server

**API specification**

[SwaggerHub](https://app.swaggerhub.com/apis/fmuratori/multiplayer-minesweeper-session-service/1.0.0)

---

**To build the application**

`
./gradlew build
`

**To run the application**

`
./gradlew run
`

**To run tests**

`
./gradlew test
`

---

**Build docker image**

`
docker build -t mmsession . --no-cache
`

**Running docker container**

Flags:
- -p      : map host ports to container ports
- -it     : for interactive mode
- --rm    : to remove previous versions of the container
- --name  : give a name to the container 
- --net   : specify the container network

`
docker run \
    -p 8001:8001 \
    -p 8002:8002 \
    -it \
    --rm \
    --network mmnetwork \
    --hostname mmsession \
    --name mmsession \
    mmsession
`