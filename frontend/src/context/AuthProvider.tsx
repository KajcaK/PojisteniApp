import { useEffect, useState, type ReactNode, type JSX } from "react";
import { api } from "../api/axios";
import type { AuthUser } from "../types/account";
import { AuthContext, type AuthContextValue } from "./AuthContext";

interface AuthProviderProps {
    children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps): JSX.Element {
    const [user, setUser] = useState<AuthUser | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let isMounted = true;

        api.get<AuthUser>("/account/me")
            .then((res) => {
                if (!isMounted) return;
                const user = res.data;
                setUser(user && typeof user === "object" && (user as any).email ? user : null);
            })
            .catch(() => {
                if (isMounted) setUser(null);
            })
            .finally(() => {
                if (isMounted) setLoading(false);
            });

        return () => {
            isMounted = false;
        };
    }, []);

    const login: AuthContextValue["login"] = async (email, password) => {
        const res = await api.post<AuthUser>("/account/login", { email, password });
        setUser(res.data);
    };

    const logout: AuthContextValue["logout"] = async () => {
        await api.post<void>("/account/logout");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
