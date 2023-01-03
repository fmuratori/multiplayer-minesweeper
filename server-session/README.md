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
docker build -t mm-server-session .
`

**Running docker container, symbolic name sessioneserver, source image mmm-server-session on default network BRIDGE**

`
docker run -p 8002:8002 -p 8001:8001 -it --rm --name sessionserver mm-server-session
`


**Running docker container, symbolic name sessioneserver, source image mmm-server-session on network HOST**

`
docker run -it --rm --link gameserver --network host --name sessionserver mm-server-session
`
