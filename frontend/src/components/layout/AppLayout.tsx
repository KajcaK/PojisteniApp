import { Box, Container, Toolbar } from "@mui/material";
import { Outlet } from "react-router-dom";
import TopNav from "./TopNavBar";

export default function AppLayout() {
    return (
        <Box sx={{ minHeight: "100vh" }}>
            <TopNav />
            <Toolbar />

            <Container component="main" maxWidth="lg" sx={{ py: 6 }}>
                <Outlet />
            </Container>
        </Box>
    );
}
