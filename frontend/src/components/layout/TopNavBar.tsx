import { AppBar, Toolbar, Typography, Button, Box, Stack } from "@mui/material";
import { NavLink } from "react-router-dom";

const TopNav = () => {
    return (
        <AppBar>
            <Toolbar
                sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 2,
                }}
            >
                {/* LEFT: App name */}
                <Box
                    sx={{
                        flex: 1,
                        display: "flex",
                        justifyContent: "flex-start",
                    }}
                >
                    <Typography
                        variant="h6"
                        component={NavLink}
                        to="/"
                        style={{ textDecoration: "none" }}
                        sx={{
                            fontWeight: 700,
                            letterSpacing: 0.5,
                            color: "primary.main",
                            "&:hover": { opacity: 0.9 },
                        }}
                    >
                        PojištěníApp
                    </Typography>
                </Box>

                {/* CENTER: Navigation */}
                <Box
                    sx={{
                        flex: 1,
                        display: "flex",
                        justifyContent: "center",
                    }}
                >
                    <Stack direction="row" spacing={2}>
                        <Button
                            component={NavLink}
                            to="/"
                            variant="text"
                            sx={{
                                color: "text.secondary",
                                fontSize: 14,
                                fontWeight: 500,
                                textTransform: "none",
                            }}
                        >
                            Home
                        </Button>

                        <Button
                            component={NavLink}
                            to="/policies"
                            variant="text"
                            sx={{
                                color: "text.secondary",
                                fontSize: 14,
                                fontWeight: 500,
                                textTransform: "none",
                            }}
                        >
                            Policies
                        </Button>

                        <Button
                            component={NavLink}
                            to="/events"
                            variant="text"
                            sx={{
                                color: "text.secondary",
                                fontSize: 14,
                                fontWeight: 500,
                                textTransform: "none",
                            }}
                        >
                            Events
                        </Button>

                        <Button
                            component={NavLink}
                            to="/about"
                            variant="text"
                            sx={{
                                color: "text.secondary",
                                fontSize: 14,
                                fontWeight: 500,
                                textTransform: "none",
                            }}
                        >
                            About
                        </Button>
                    </Stack>
                </Box>

                {/* RIGHT: Auth buttons */}
                <Box
                    sx={{
                        flex: 1,
                        display: "flex",
                        justifyContent: "flex-end",
                        gap: 1.5,
                    }}
                >
                    <Button
                        component={NavLink}
                        to="/change-password"
                        variant="text"
                        sx={{
                            color: "text.secondary",
                            fontSize: 14,
                            fontWeight: 500,
                            textTransform: "none",
                        }}
                    >
                        Change Password
                    </Button>

                    <Button
                        component={NavLink}
                        to="/login"
                        variant="text"
                        sx={{
                            color: "text.secondary",
                            fontSize: 14,
                            fontWeight: 500,
                            textTransform: "none",
                        }}
                    >
                        Login
                    </Button>

                    <Button
                        component={NavLink}
                        to="/register"
                        variant="contained"
                        color="primary"
                        sx={{
                            fontSize: 14,
                            fontWeight: 500,
                            textTransform: "none",
                        }}
                    >
                        Register
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default TopNav;
