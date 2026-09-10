import { createContext, useContext, useState, useEffect } from "react";
import { decodeToken } from "../utils/jwt";
import { refreshAccessToken, clearTokens } from "../utils/tokenManager";

const AuthContext = createContext(null);

export function AuthProvider({children}) {
    const [isAuthenticated, setAuthenticated] = useState(false);
    const [role, setRole] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const applyToken = (token) => {
        const decoded = decodeToken(token);
        if (!decoded) {
            return false;
        }
        setAuthenticated(true);
        setRole(decoded.role ?? null);
        return true;
    }
    useEffect(() => {
        async function initializeAuth() {
            const accessToken = localStorage.getItem("accessToken");
            const refreshToken = localStorage.getItem("refreshToken");
            if(!accessToken) {
                setIsLoading(false);
                return;
            }
            if(applyToken(accessToken)) {
                setIsLoading(false);
                return;
            }
            if(refreshToken) {
                try {
                    const newAccessToken = await refreshAccessToken();
                    applyToken(newAccessToken);
                }catch(err) {
                    clearTokens();
                    setAuthenticated(false);
                    setRole(null);
                }
            }
            setIsLoading(false);
        }
        initializeAuth()
    }, []);
    const login = (accessToken, refreshToken) => {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
        applyToken(accessToken);
    };
    const logout = () => {
        clearTokens();
        setAuthenticated(false);
        setRole(null);
    }
    return (
        <AuthContext.Provider value = {{isAuthenticated, role, isLoading, login, logout}}>
            {children}
        </AuthContext.Provider>
    )
}
export function useAuth() {
    return useContext(AuthContext);
}