import React from 'react'

const io = require("socket.io-client/dist/socket.io")

export const gameSocket = io("127.0.0.1:8004", { 
    autoConnect: false, 
    reconnectionAttempt: 5});
export const GameSocketContext = React.createContext(null)