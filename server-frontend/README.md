# Multiplayer Minesweeper - Game server

**API specification**

[SwaggerHub](https://app.swaggerhub.com/apis/fmuratori/multiplayer-minesweeper-game-service/1.0.0)

---

**To build the application**

`npm run build`

Be sure to build the frontend independent project to the latest version.

`cd ../frontend/`
`npm run build`

---

**Build docker image**

`
docker build -t mm-server-frontend .
`

---

**Running docker container, symbolic name frontendserver, source image mmm-server-frontend on network HOST**


`
docker run -it --rm --network host --name frontendserver mm-server-frontend
`