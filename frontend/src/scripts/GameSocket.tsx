import React from 'react'

const io = require("socket.io-client/dist/socket.io")

export const gameSocket = io(process.env.REACT_APP_GAME_SOCKET, { 
    autoConnect: false, 
    reconnectionAttempt: 5});
export const GameSocketContext = React.createContext(null)