import { createContext, useContext, useState, useEffect } from "react";
import { decodeToken } from "../utils/jwt";

const AuthContext = createContext(null);

export function AuthProvider({children}) {
    const [isAuthenticated, setAuthenticated] = useState(false);
    const [role, setRole] = useState(null);
    const [accountType, setAccountType] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const applyToken = (token) => {
        const decoded = decodeToken(token);
        if (!decoded) {
            return false;
        }
        setAuthenticated(true);
        setRole(decoded.role ?? null);
        setAccountType(decoded.accountType ?? null);
        return true;
    }
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if(token) {
            const is = applyToken(token);
            if(!is) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
            }
        }
        setIsLoading(false);
    }, []);
    const login = (accessToken, refreshToken) => {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
        applyToken(accessToken);
    };
    const logout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        setAuthenticated(false);
        setRole(null);
        setAccountType(null);
    }
    return (
        <AuthContext.Provider value = {{isAuthenticated, role, accountType, isLoading, login, logout}}>
            {children}
        </AuthContext.Provider>
    )
}
export function useAuth() {
    return useContext(AuthContext);
}