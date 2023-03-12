# Multiplayer Minesweeper - Session server

This project contains the logic of a distributed system for a microservices oriented platform for a multiplayer minesweeper game.
Here the matchmaking and "waiting rooms" logic of the application are handled and the main components of this project are:
- a bidirectional socket implemented with Socket.IO
- a basic REST API implemented with Vert.X
- the logic for the management of multiple sessions, users matchmaking and games initialization

# API specification

[SwaggerHub](https://app.swaggerhub.com/apis/fmuratori/multiplayer-minesweeper-session-service/1.0.0)

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

# Execution inside a docker container

**0. Create a user defined-network**

This container uses a user-defined bridge network to allow an easier management of connections toward other containers of the same micro-serve platform.
This step should be executed only once.

``

**1. Build the docker image**

`
docker build -t mmsession . --no-cache
`

Flags:
- -t            : the image tag
- --no-cache    : force code changes to be applied

**2. Running docker container**

````  
docker run \
    -p 8001:8001 \
    -p 8002:8002 \
    -it \
    --rm \
    --network mmnetwork \
    --hostname mmsession \
    mmsession
````

Flags:
- -p           : map host ports to container ports
- -it          : for interactive mode
- --rm         : to remove previous versions of the container
- --network    : the user defined network name (look at step 0)
- --hostname   : name of the container inside the network

# Execution with docker-compose

Alternatively, you can easily deploy the entire platform using docker compose. The configuration file is inside the main folder of this project.
To run the docker-compose use the following commands:

`docker compose build --no-cache`

`docker compose up`