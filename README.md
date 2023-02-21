**To run each container separately**

Create a user-defined docker bridge network

`
docker network create --driver bridge --subnet=172.18.0.0/16 mmnetwork
`

Build and run each container. You can find the `docker build ...` and `docker run ...` commands inside the containers folders.  

**Use docker-compose to start the entire system**

NB: You may need to delete the existing mmnetwork

`
docker compose up
`

**NOTE: containers host configuration**

Hosts configuration:
- frontend server at port 172.18.0.10
- session server at port 172.18.0.11
- game server at port 172.18.0.12

Ports configuration for server session:
- port 3000 for HTTP requests

Ports configuration for server session:
- port 8001 for REST API calls
- port 8002 for Socket.IO channels

Ports configuration for server game:
- port 8003 for REST API calls
- port 8004 for Socket.IO channels


