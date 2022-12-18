import React from 'react'

const io = require("socket.io-client/dist/socket.io")

export const sessionSocket = io("127.0.0.1:8002/session", { 
    autoConnect: false, 
    reconnectionAttempt: 5});

export const browseSessionsSocket = io("127.0.0.1:8002/browse", {
    autoConnect: false,
    reconnectionAttempt:5});

export const SessionSocketContext = React.createContext(null)

export const browseSessionsSocketContext = React.createContext(null)