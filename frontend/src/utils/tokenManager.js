import axios from "axios";
import env from "../config/env";

let isRefreshing = false;
let pendingRequests = [];

async function refreshAccessToken() {
    if(isRefreshing) {
        return new Promise((resolve, reject) => {
            pendingRequests.push({resolve, reject});
        });
    }
    isRefreshing = true;
    try {
        const refreshToken = localStorage.getItem("refreshToken");
        const res = await axios.post(`${env.API_BASE_URL}/auth/refresh`, {
            token: refreshToken,
        });
        const newAccessToken = res.data.accessToken;
        localStorage.setItem("accessToken", newAccessToken);
        pendingRequests.forEach((request) => {
            request.resolve(newAccessToken);
        });
        pendingRequests = [];
        return newAccessToken;
    }catch(err) {
        pendingRequests.forEach((request) => {
            request.reject(err);
        });
        pendingRequests = [];
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        throw err;
    } finally {
        isRefreshing = false;
    }
}
function clearTokens() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}

export {refreshAccessToken, clearTokens};