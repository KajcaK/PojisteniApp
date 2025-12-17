import { Box, Container, Toolbar } from "@mui/material";
import { Outlet } from "react-router-dom";
import TopNav from "./TopNavBar.tsx";

export default function AuthLayout() {
    return (
        <Box sx={{ minHeight: "100vh" }}>
            <TopNav />
            <Toolbar />

            <Box sx={{ display: "flex", alignItems: "center", minHeight: "calc(100vh - var(--mui-toolbar-height, 64px))" }}>
                <Container maxWidth="sm" sx={{ py: 6 }}>
                    <Outlet />
                </Container>
            </Box>
        </Box>
    );
}
