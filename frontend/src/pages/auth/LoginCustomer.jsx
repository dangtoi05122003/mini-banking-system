import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { authApi } from "../../api/authApi";
import { useAuth } from "../../context/AuthContext";
import { ROUTE_PATHS } from "../../routes/RoutePaths";
import "../../styles/LoginCustomer.css";

export default function LoginCustomer() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || ROUTE_PATHS.CUSTOMER_HOME;
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!username.trim()) {
            setError("Vui lòng nhập tên đăng nhập");
            return;
        }
        if (!password.trim()) {
            setError("Vui lòng nhập mật khẩu");
            return;
        }
        try {
            setLoading(true);
            setError("");
            const { data } = await authApi.loginCustomer(username, password);
            login(data.accessToken, data.refreshToken);
            navigate(from, { replace: true });
        } catch (err) {
            setError(err.response?.data?.message || err.response?.data?.error || "Đăng nhập thất bại");
        } finally {
            setLoading(false);
        }
    };
    return (
        <div className="login-container">
            <div className="login-card">
                <h2 className="login-title">Đăng nhập khách hàng</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Username</label>
                        <input className="form-input" value={username} onChange={(e) => setUsername(e.target.value)}/>
                    </div>
                    <div className="form-group">
                        <label>Password</label>
                        <input className="form-input" type="password" value={password} onChange={(e) => setPassword(e.target.value)}/>
                    </div>
                    {error && <p className="error-message">{error}</p>}
                    <button className="login-button" type="submit" disabled={loading}>
                        {loading ? "Logging in..." : "Login"}
                    </button>
                </form>
            </div>
        </div>
    );
}