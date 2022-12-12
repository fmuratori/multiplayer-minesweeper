// import express, { Express, Request, Response } from 'express';
// import dotenv from 'dotenv';

// dotenv.config();

// const app: Express = express();
// const port = process.env.PORT;

// app.get('/', (req: Request, res: Response) => {
//   res.send('Express + TypeScript Server');
// });

// app.listen(port, () => {
//   console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
// });

// =========

// import path from 'path';
// import express, { Express, Request, Response } from 'express';
// import dotenv from 'dotenv';

// dotenv.config();

// const app: Express = express();
// const port = process.env.PORT;

// app.use(express.static(path.join(__dirname, '..', '..', 'frontend', 'build')));

// app.get('/', function (req: Request, res:Response) {
//   res.sendFile(path.join(__dirname, '..', '..', 'frontend', 'build', 'index.html'));
// });

// app.listen(port, () => {
//   console.log(`⚡️[server]: Server is running at http://127.0.0.1:${port}`);
// });

// ======================

import { createServer } from "http";
import { Server } from "socket.io";

const httpServer = createServer();
const io = new Server(httpServer, { cors: { origin : '*',}});

io.on('connection', (socket) => {
  console.log('a user connectedddddd');

  socket.on('disconnect', () => {
    console.log('user disconnected');
  });

  socket.on('chat message', (msg) => {
    io.emit('chat message', msg);
  });
});

httpServer.listen(8004);