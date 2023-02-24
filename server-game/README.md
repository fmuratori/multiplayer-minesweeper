# Multiplayer Minesweeper - Game server

This project contains the logic of a distributed system for a microservices oriented platform for multiplayer minesweeper game.
Here the games instances and states are handled. 
The main components of this project are:
- a bidirectional socket implemented with Socket.IO
- a basic REST API implemented with Vert.X
- the logic for the management of many game instances

# API specification

[SwaggerHub](https://app.swaggerhub.com/apis/fmuratori/multiplayer-minesweeper-game-service/1.0.0)

---

# Execution with gradle

**To build the application**

`
gradlew build
`

**To run the application**

`
./gradlew run
`

**To run tests**

`
./gradlew test
`

# Execution with a docker container

**NB**: be sure to have instantiated a user defined bridge network. For more details, check the README file inside 
the project main folder.

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

# Execution with docker-compose

The configuration of the docker compose file is in the project main folder.