import { Routes, Route, Navigate } from "react-router-dom";

import LoginCustomer from "../pages/auth/LoginCustomer";
import LoginStaff from "../pages/auth/LoginStaff";
import HomeCustomer from "../pages/customer/Home";
import HomeStaff from "../pages/staff/Home";
import Layout from "../components/layout/Layout";
import PrivateRoute from "./PrivateRoute";
import { ROUTE_PATHS } from "./RoutePaths";

function AppRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path={ROUTE_PATHS.LOGIN_CUSTOMER} element={<LoginCustomer />} />
        <Route path={ROUTE_PATHS.LOGIN_STAFF} element={<LoginStaff />} />
        <Route element={<PrivateRoute allowedAccountTypes={["USER"]} />}>
          <Route path={ROUTE_PATHS.CUSTOMER_HOME} element={<HomeCustomer />} />
        </Route>
        <Route element={<PrivateRoute allowedAccountTypes={["STAFF"]} />}>
          <Route path={ROUTE_PATHS.STAFF_HOME} element={<HomeStaff />} />
        </Route>
        <Route path={ROUTE_PATHS.UNAUTHORIZED} element={<h1>Unauthorized</h1>} />
        <Route path="*" element={<h1>404 - Page Not Found</h1>} />
      </Route>
    </Routes>
  );
}

export default AppRoutes;