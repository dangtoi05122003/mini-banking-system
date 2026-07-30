import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { authApi } from "../../api/authApi";
import { useAuth } from "../../context/AuthContext";
import { ROUTE_PATHS } from "../../routes/RoutePaths";
import "../../styles/LoginStaff.css";

export default function LoginStaff() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || ROUTE_PATHS.STAFF_HOME;
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
            const { data } = await authApi.loginStaff(username, password);
            login(data.accessToken, data.refreshToken);
            navigate(from, { replace: true });
        } catch (err) {
            setError(err.response?.data?.message || err.response?.data?.error || "Đăng nhập thất bại");
        } finally {
            setLoading(false);
        }
    };
    return (
        <div className="staff-login-container">
            <div className="staff-login-card">
                <h2 className="staff-login-title">Đăng nhập nhân viên</h2>
                <form onSubmit={handleSubmit}>
                    <div className="staff-form-group">
                        <label>Username</label>
                        <input className="staff-form-input" value={username} onChange={(e)=>setUsername(e.target.value)}/>
                    </div>
                    <div className="staff-form-group">
                        <label>Password</label>
                        <input className="staff-form-input" type="password" value={password} onChange={(e)=>setPassword(e.target.value)}/>
                    </div>
                    {error && <p className="staff-error"> {error} </p>}
                    <button className="staff-login-button" disabled={loading}>
                        {loading ? "Logging in..." : "Login"}
                    </button>
                </form>
            </div>
        </div>
    );
}