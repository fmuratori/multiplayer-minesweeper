# Multiplayer Minesweeper - Game server

**API specification**

[SwaggerHub](https://app.swaggerhub.com/apis/fmuratori/multiplayer-minesweeper-game-service/1.0.0)

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
docker build -t mmgame .
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
    -p 8003:8003 \
    -p 8004:8004 \
    -it \
    --rm \
    --network mmnetwork \
    --ip 172.18.0.12 \
    --name mmgame \
    mmgame
`