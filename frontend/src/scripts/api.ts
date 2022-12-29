import axios from "axios";

// move baseULR into config file
const baseURL = "http://127.0.0.1:8001";


export function getSessions() {
    return axios.get(baseURL + '/sessions')
};

export function postNewSession(payload) {
    return axios.post(baseURL + '/new-session', payload)
};