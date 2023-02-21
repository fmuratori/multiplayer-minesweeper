import React from 'react'

const io = require("socket.io-client/dist/socket.io")

export const sessionSocket = io(process.env.REACT_APP_SESSION_SOCKET+"/session", { 
    autoConnect: false, 
    reconnectionAttempt: 5});

export const browseSessionsSocket = io(process.env.REACT_APP_SESSION_SOCKET + "/browse", {
    autoConnect: false,
    reconnectionAttempt:5});

export const SessionSocketContext = React.createContext(null)

export const browseSessionsSocketContext = React.createContext(null)