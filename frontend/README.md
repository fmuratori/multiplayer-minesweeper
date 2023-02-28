# Multiplayer Minesweeper - Frontend

**API specification**

[SwaggerHub](https://app.swaggerhub.com/apis/fmuratori/multiplayer-minesweeper-game-service/1.0.0)

---

**To build the application**

`npm run build`

**To start the application**

`npm run start`

---

**Build docker image**

`
docker build -t mmfrontend . --no-cache
`


**Running docker container, symbolic name sessioneserver, source image mmmsession on default network BRIDGE**

Flags:
- -p      : map host ports to container ports
- -it     : for interactive mode
- --rm    : to remove previous versions of the container
- --name  : give a name to the container 
- --net   : specify the container network

`
docker run \
    -p 3000:3000 \
    -it \
    --rm \
    --network mmnetwork \
    --hostname mmfrontend \
    --name mmfrontend \
    mmfrontend
`

docker run \
    -p 3000:3000 \
    -it \
    --rm \
    --network mmnetwork \
    --name mmfrontend \
    --hostname mmfrontend \
    mmfrontend