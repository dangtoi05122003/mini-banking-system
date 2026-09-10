import axios from "axios"
import env from "../config/env"
import { refreshAccessToken } from "../utils/tokenManager";

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

axiosClient.interceptors.response.use((response) => response, async (err) => {
    async(err) => {
        const originalRequest = err.config;
        if (err.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const newAccessToken = await refreshAccessToken();
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                return axiosClient(originalRequest);
            } catch (refreshError) {
                window.location.href = "/login";
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(err);
    }
});

export default axiosClient;