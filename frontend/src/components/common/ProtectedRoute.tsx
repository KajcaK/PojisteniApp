import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { Loader } from "./Loader";
import { useAuth } from "../../context/useAuth";

interface ProtectedRouteProps {
    children: ReactNode;
    redirectTo?: string;
}

export function ProtectedRoute({
                                   children,
                                   redirectTo = "/login",
                               }: ProtectedRouteProps) {
    const { user, loading } = useAuth();

    if (loading) return <Loader fullHeight />;

    if (!user?.email) {
        return <Navigate to={redirectTo} replace />;
    }

    return <>{children}</>;
}
