import axios from "axios";

const sessionsServerBaseURL = process.env.REACT_APP_SESSION_API;
const gameServerBaseURL = process.env.REACT_APP_GAME_API;


export function getSessions() {
    return axios.get('http://' + sessionsServerBaseURL + '/sessions')
};

export function postNewSession(payload) {
    return axios.post('http://' + sessionsServerBaseURL + '/new-session', payload)
};

export function getGameModes() {
    return axios.get('http://' + gameServerBaseURL + '/game-modes')
};