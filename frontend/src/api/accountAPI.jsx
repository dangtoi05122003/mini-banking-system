import axiosClient from "./axiosClient"

export const accountAPI = {
    createAccount:() => axiosClient.post("/account"),
    getMyAccounts: () => axiosClient.get("/account/me"),
    setPrimaryAccount: (accountNumber) => axiosClient.put(`/account/${accountNumber}/primary`)
}