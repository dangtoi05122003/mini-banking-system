import axiosClient from "./axiosClient";

export const authApi = {
    loginCustomer: (username, password) => axiosClient.post("/auth/customer", {username, password}),
    loginStaff: (username, password) => axiosClient.post("/auth/staff", {username, password}),
    refreshToken: (token) => axiosClient.post("/auth/refresh", {token}),
    logout: (accessToken) => axiosClient.post("/auth/logout", { accessToken })
};