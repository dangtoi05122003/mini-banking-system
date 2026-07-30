import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { ROUTE_PATHS } from "../../routes/RoutePaths";
import "../../styles/Header.css";
export default function Header() {
    const {logout, accountType, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const handleLogout = () => {
        logout();
        if(accountType === "STAFF") {
            navigate(ROUTE_PATHS.LOGIN_STAFF)
        } else {
            navigate(ROUTE_PATHS.LOGIN_CUSTOMER)
        }
    };
    const handleLogin = () => {
        navigate(ROUTE_PATHS.LOGIN_CUSTOMER);
    };
    return (
        <header className="header">
            <h2 className="logo">ZSB</h2>
            <div className="header-right">
                {isAuthenticated ? (<button className="logout-btn" onClick={handleLogout}>Logout</button>)
                : (<button className="login-btn" onClick={handleLogin}>Login</button>)
                }
            </div>
        </header>
    )
}