import { Navigate, Outlet, useLocation } from "react-router-dom";
import {ROUTE_PATHS} from './RoutePaths';
import { useAuth } from "../context/AuthContext";

function PrivateRoute ({allowedAccountTypes}) {
    const {isAuthenticated, accountType, isLoading} = useAuth();
    const location = useLocation();
    if(isLoading) return <div>Loading ....</div>
    if(!isAuthenticated) {
        const loginPath = allowedAccountTypes?.includes("STAFF") ? ROUTE_PATHS.LOGIN_STAFF : ROUTE_PATHS.LOGIN_CUSTOMER;
        return <Navigate to= {loginPath} state={{from: location}} replace />
    }
    if (allowedAccountTypes && !allowedAccountTypes.includes(accountType)) {
        return <Navigate to={ROUTE_PATHS.UNAUTHORIZED} replace />;
    }
    return <Outlet />;
}
export default PrivateRoute;