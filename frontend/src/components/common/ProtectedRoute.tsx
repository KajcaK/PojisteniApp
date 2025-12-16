import { Navigate, Outlet } from "react-router-dom";
import {useAuth} from "../../context/AuthContext.tsx";

interface ProtectedRouteProps {
    redirectTo?: string;
}

export function ProtectedRoute({ redirectTo = "/login" }: ProtectedRouteProps) {
    const { user, loading } = useAuth();

    if (loading) return <div>Loading...</div>;
    if (!user) return <Navigate to={redirectTo} replace />;

    return <Outlet />;
}
