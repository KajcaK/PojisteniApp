import {
    createContext,
    useContext,
    useEffect,
    useState,
    type ReactNode, type JSX,
} from "react";
import { api } from "../api/axios";
import type { AuthUser } from "../types/account.ts";

interface AuthContextValue {
    user: AuthUser | null;
    loading: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps): JSX.Element {
    const [user, setUser] = useState<AuthUser | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    // auto-check session on refresh
    useEffect(() => {
        let isMounted = true;

        api.get<AuthUser>("/account/me")
            .then(res => {
                if (isMounted) {
                    setUser(res.data);
                }
            })
            .catch(() => {
                if (isMounted) {
                    setUser(null);
                }
            })
            .finally(() => {
                if (isMounted) {
                    setLoading(false);
                }
            });

        return () => {
            isMounted = false;
        };
    }, []);

    const login = async (email: string, password: string): Promise<void> => {
        const res = await api.post<AuthUser>("/account/login", {
            email,
            password,
        });
        setUser(res.data);
    };

    const logout = async (): Promise<void> => {
        await api.post<void>("/account/logout");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return ctx;
}