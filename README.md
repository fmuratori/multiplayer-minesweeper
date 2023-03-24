# PROJECT GOAL AND MAIN STRUCTURE

Minesweeper is a classic Windows game built-in and shipped with the Windows 97 operating system. The game consists of a board of tiles and each tile may or may not contain a mine. If the player clicks the wrong tile, the game ends but if an empty tile is selected, then the board shows how many mines are around it. The player wins the game if he locates all the mines with flags and inspects all the other tiles that do not contain mines.

In particular, the goal of this project is to allow multiple users to play the same game of minesweeper and collaborate to solve the puzzle. To do that, the systed designed is divided into 3 different subprojects, each solving a subset of the whole problem:
- a game-core service implements the core concepts of the game and allows users to execute actions on a shared game board;
- a session service implements the waiting phase prior to a match and the synchronous initialization of a particular match;
- a web based frontend service allows users to acces the game through a simple and intuitive interface.

The learning goals of this project is to experiment with the distributed systems concept and find solutions to allow a shared game state, consistent and available to multiple users. Another interesting aspect of the development is the integration of the software with technologies such as Docker and GitLab CI/CD.

# SESSIONS AND GAME APIs SPECFICATIONS
For the sessions service APIs, the endpoints specifications are visible at the following link:
[SwaggerHub](https://app.swaggerhub.com/apis-docs/fmuratori/multiplayer-minesweeper-session-service/1.0.0)

For the games service APIs, the endpoints specifications are visible at the following link:
[SwaggerHub](https://app.swaggerhub.com/apis-docs/fmuratori/multiplayer-minesweeper-game-service/1.0.0)
# HOW TO RUN

The platform can be run in 3 different ways:
- with independently initialized microservices build on top of automatic build tools such as npm for the frontend and gradle for the session and game services;
- with independently initialized and managed Docker containers;
- through docker-compose.

The first approach does not allow the integration of the 3 components since the implementation requires a DNS name resolutor to identify the single hosts IPs. This approach is used to rapidly check tests and builds results.    

## RUN DOCKER CONTAINERS SEPARATELY

**STEP 1**
Create a user-defined docker bridge network:

`
docker network create --driver bridge --subnet=172.18.0.0/16 mmnetwork
`

**STEP 2**

Build and run each container. You can find the `docker build ...` and `docker run ...` commands inside the README.md file of each sibproject folder.  


## ... OR USE DOCKER COMPOSE

Use docker-compose to start the entire system:

**STEP 1**
`
docker compose build --no-cache
`

**STEP 2**
`
docker compose up
`

# NOTE

**Containers host configuration**

The application consists of 3 separate microservices, each enclosed inside a docker container:
- *frontend* provides a web application
- *session* provides REST API and Socket.IO channels and is dedicated to the management of match-creation and waiting lists
- *game* provides REST API and Socket.IO channels and is dedicated to the management of game instances

Hosts configuration:
- *frontend* is named `mmfrontend`
- *session* is named `mmsession`
- *game* is named `mmgame`

Ports configuration for server session:
- port 3000 for HTTP requests

Ports configuration for server session:
- port 8001 for REST API calls
- port 8002 for Socket.IO channels

Ports configuration for server game:
- port 8003 for REST API calls
- port 8004 for Socket.IO channels


