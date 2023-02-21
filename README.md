# RUN DOCKER CONTAINERS SEPARATELY

**STEP 1**
Create a user-defined docker bridge network

`
docker network create --driver bridge --subnet=172.18.0.0/16 mmnetwork
`

**STEP 2**

Build and run each container. You can find the `docker build ...` and `docker run ...` commands inside the README.md file of each container folder.  


# ... OR USE DOCKER COMPOSE

Use docker-compose to start the entire system:

**STEP 1**
`
docker compose build
`

**STEP 2**
`
docker compose up
`

# NOTE

**Containers host configuration**

The application consists of 3 separate microservices, each enclosed inside a docker container:
- *frotend* provides the frotend web application
- *session* provides REST API and Socket.IO channels and is dedicated to the management of match-creation and waiting lists
- *game* provides REST API and Socket.IO channels and is dedicated to the management of game instances

Hosts configuration:
- *frontend* server at port 172.18.0.10
- *session* server at port 172.18.0.11
- *game* server at port 172.18.0.12

Ports configuration for server session:
- port 3000 for HTTP requests

Ports configuration for server session:
- port 8001 for REST API calls
- port 8002 for Socket.IO channels

Ports configuration for server game:
- port 8003 for REST API calls
- port 8004 for Socket.IO channels


