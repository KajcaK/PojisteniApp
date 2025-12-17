import { AppBar, Toolbar, Typography, Button, Box, Stack } from "@mui/material";
import { NavLink } from "react-router-dom";

const linkSx = {
    textDecoration: "none",
};

const TopNav = () => {
    return (
        <AppBar>
            <Toolbar sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                {/* Brand */}
                <Box sx={{ flex: 1, display: "flex", justifyContent: "flex-start" }}>
                    <Typography
                        variant="h5"
                        component={NavLink}
                        to="/"
                        sx={{
                            ...linkSx,
                            color: "text.primary",
                            letterSpacing: "-0.01em",
                        }}
                    >
                        PojištěníApp
                    </Typography>
                </Box>

                {/* Primary nav */}
                <Box sx={{ flex: 1, display: "flex", justifyContent: "center" }}>
                    <Stack direction="row" spacing={0.5}>
                        {[
                            { to: "/", label: "Home" },
                            { to: "/policies", label: "Policies" },
                            { to: "/events", label: "Events" },
                            { to: "/about", label: "About" },
                        ].map((item) => (
                            <Button
                                key={item.to}
                                component={NavLink}
                                to={item.to}
                                variant="text"
                                size="small"
                                sx={{
                                    px: 1.25,
                                    "&.active": {
                                        backgroundColor: "action.selected",
                                    },
                                }}
                            >
                                {item.label}
                            </Button>
                        ))}
                    </Stack>
                </Box>

                {/* Auth actions */}
                <Box sx={{ flex: 1, display: "flex", justifyContent: "flex-end", gap: 1 }}>
                    <Button component={NavLink} to="/change-password" variant="text" size="small">
                        Change Password
                    </Button>

                    <Button
                        component={NavLink}
                        to="/login"
                        variant="text"
                        size="small"
                        sx={{
                            "&.active": {
                                backgroundColor: "action.selected",
                            },
                        }}
                    >
                        Login
                    </Button>

                    <Button component={NavLink} to="/register" variant="contained" size="small">
                        Register
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default TopNav;
