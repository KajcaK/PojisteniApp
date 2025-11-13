import { Routes, Route } from "react-router-dom";
import { Box } from "@mui/material";

import HomePage from "./pages/HomePage";
import PoliciesPage from "./pages/PoliciesPage";
import EventsPage from "./pages/EventsPage";
import AboutPage from "./pages/AboutPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import TopNav from "./components/layout/TopNavBar.tsx";
import ChangePasswordPage from "./pages/ChangePasswordPage.tsx";


export default function App() {
  return (
    <>
      {/* top navigation bar */}
        <TopNav />

      {/* page content */}
      <Box sx={{ mt: 10, p: 3 }}>
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/policies" element={<PoliciesPage />} />
            <Route path="/events" element={<EventsPage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route path="/change-password" element={<ChangePasswordPage />} />
        </Routes>
      </Box>
    </>
  );
}
