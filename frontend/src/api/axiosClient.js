import axios from "axios"
import env from "../config/env"

const axiosClient = axios.create({
    baseURL: env.API_BASE_URL,
    headers: { "Content-Type": "application/json" }
});

axiosClient.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");
    if(token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

let isRefreshing = false;
let pendingRequests = [];
axiosClient.interceptors.response.use((response) => response, async (err) => {
    const originalRequest = err.config;
    if (err.response?.status === 401 && !originalRequest._retry) {
        if (isRefreshing) {
            return new Promise((resolve, reject) => {
                pendingRequests.push({resolve, reject});
            }).then((newToken) => {
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return axiosClient(originalRequest);
            });
        }
        originalRequest._retry = true;
        isRefreshing = true;
        try {
            const refreshToken = localStorage.getItem("refreshToken");
            const res = await axios.post(`${env.baseURL}/auth/refresh`, {
                token: refreshToken,
            });
            const newAccessToken = res.data.accessToken;
            localStorage.setItem("accessToken", newAccessToken);
            pendingRequests.forEach((req) => req.resolve(newAccessToken));
            pendingRequests = [];
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return axiosClient(originalRequest);
        } catch (refreshError) {
            pendingRequests.forEach((req) => req.reject(refreshError));
            pendingRequests = [];
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href = "/login";
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
    return Promise.reject(err);
});

export default axiosClient;