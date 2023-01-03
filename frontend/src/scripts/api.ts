import axios from "axios";

const sessionsServerBaseURL = process.env.REACT_APP_SESSION_SERVER_URL;
const gameServerBaseURL = process.env.REACT_APP_GAME_SERVER_URL;


export function getSessions() {
    return axios.get(sessionsServerBaseURL + '/sessions')
};

export function postNewSession(payload) {
    return axios.post(sessionsServerBaseURL + '/new-session', payload)
};

export function getGameModes() {
    return axios.get(gameServerBaseURL + '/game-modes')
};