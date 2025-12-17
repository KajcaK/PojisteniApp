import { Routes, Route } from "react-router-dom";

import AppLayout from "./components/layout/AppLayout";
import AuthLayout from "./components/layout/AuthLayout";

import HomePage from "./pages/HomePage";
import PoliciesPage from "./pages/PoliciesPage";
import EventsPage from "./pages/EventsPage";
import AboutPage from "./pages/AboutPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ChangePasswordPage from "./pages/ChangePasswordPage";

import { ProtectedRoute } from "./components/common/ProtectedRoute";

export default function App() {
    return (
        <Routes>
            {/* Auth-style pages */}
            <Route element={<AuthLayout />}>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                {/* Auth-style but protected */}
                <Route
                    path="/change-password"
                    element={
                        <ProtectedRoute>
                            <ChangePasswordPage />
                        </ProtectedRoute>
                    }
                />
            </Route>

            {/* Main app */}
            <Route element={<AppLayout />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/policies" element={<PoliciesPage />} />
                <Route path="/events" element={<EventsPage />} />
                <Route path="/about" element={<AboutPage />} />
            </Route>
        </Routes>
    );
}
