import axios from "axios";

// move baseULR into config file
const sessionsServerBaseURL = "http://127.0.0.1:8001";
const gameServerBaseURL = "http://127.0.0.1:8003";


export function getSessions() {
    return axios.get(sessionsServerBaseURL + '/sessions')
};

export function postNewSession(payload) {
    return axios.post(sessionsServerBaseURL + '/new-session', payload)
};

export function getGameModes() {
    return axios.get(gameServerBaseURL + '/game-modes')
};