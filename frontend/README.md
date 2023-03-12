# Multiplayer Minesweeper - Frontend

This project contains the implementation of a distributed system for a microservices oriented platform for multiplayer minesweeper game.
In this folder is defined the frontend of the application and the communication with the backend servers (sessions and game core) are implemented via  bidirectional socket channels and rest api calls.

# Execution with npm

**To build the application**

`npm run build`

**To start the application**

`npm run start`

# Execution inside of a docker container

**0. Create a user defined-network**

This container uses a user-defined bridge network to allow an easier management of connections toward other containers of the same micro-serve platform. 
This step should be executed only once.

``

**1. Build the docker image**

`
docker build -t mmfrontend . --no-cache
`

Flags:
- -t            : the image tag
- --no-cache    : force code changes to be applied

**2. Running docker container**

````  
docker run \
    -p 3000:3000 \
    -it \
    --rm \
    --network mmnetwork \
    --hostname mmfrontend \
    mmfrontend
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