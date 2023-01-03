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
docker build -t mm-server-game .
`

**Running docker container**

`
docker run -p 8003:8003 -p 8004:8004 -it --rm --name gameserver mm-server-game
`


**Running docker container, symbolic name gameserver, source image mmm-server-game on network HOST**

`
docker run -it --rm --network host --name gameserver mm-server-game
`