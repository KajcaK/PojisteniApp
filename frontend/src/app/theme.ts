import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
    palette: {
        mode: "dark",
        primary: {
            main: "#72dcf5",
        },
        background: {
            default: "#020617",
            paper: "rgba(2,6,23,0.36)",
        },
        text: {
            primary: "#f5f7fa",
            secondary: "#a0a7b5",
        },
    },
    shape: {
        borderRadius: 16,
    },
    typography: {
        fontFamily:
            "'Inter', system-ui, -apple-system, BlinkMacSystemFont, sans-serif",
        h6: {
            fontWeight: 700,
        },
    },
    components: {
        MuiAppBar: {
            defaultProps: {
                position: "fixed",
                color: "transparent",
                elevation: 0,
            },
            styleOverrides: {
                root: {
                    top: 12,
                    borderRadius: "18px",
                    zIndex: 1201,

                    background: "linear-gradient(135deg, rgba(255,255,255,0.06), rgba(0,0,0,0.35))",
                    backdropFilter: "blur(20px) saturate(180%)",
                    WebkitBackdropFilter: "blur(20px) saturate(180%)",

                    border: "1px solid rgba(255,255,255,0.15)",
                    boxShadow: "0 8px 24px rgba(0,0,0,0.55)",

                    color: "#f1f5f9",

                    "& .MuiToolbar-root": {
                        minHeight: 64,
                        paddingLeft: 20,
                        paddingRight: 20,
                    },
                },
            },
        },

        MuiToolbar: {
            styleOverrides: {
                root: {
                    minHeight: 64,
                    "@media (min-width:600px)": {
                        minHeight: 72,
                    },
                },
            },
        },

        MuiButton: {
            defaultProps: {
                disableElevation: true,
            },
            styleOverrides: {
                root: {
                    borderRadius: 999,
                    textTransform: "none",
                    fontWeight: 500,
                    paddingLeft: 20,
                    paddingRight: 20,
                    paddingTop: 8,
                    paddingBottom: 8,
                    transition: "all 0.25s ease",

                    "&:hover": {
                        transform: "translateY(-1px)",
                    },
                },
            },

            variants: [
                {
                    // <Button variant="contained" color="primary" />
                    props: { variant: "contained", color: "primary" },
                    style: {
                        background:
                            "linear-gradient(135deg, rgba(255,255,255,0.12), rgba(0,0,0,0.35))",
                        backdropFilter: "blur(16px)",
                        WebkitBackdropFilter: "blur(16px)",
                        color: "#fff",
                        border: "1px solid rgba(255,255,255,0.18)",
                        boxShadow: "0 6px 18px rgba(0,0,0,0.5)",

                        "&:hover": {
                            background:
                                "linear-gradient(135deg, rgba(255,255,255,0.18), rgba(0,0,0,0.45))",
                            boxShadow: "0 8px 24px rgba(0,0,0,0.6)",
                        },
                    },
                },
                {
                    // <Button variant="text" color="primary" />
                    props: { variant: "text", color: "primary" },
                    style: {
                        color: "#e2e8f0",
                        "&:hover": {
                            background: "rgba(255,255,255,0.05)",
                            backdropFilter: "blur(10px)",
                        },
                    },
                },
                {
                    // <Button variant="outlined" color="primary" />
                    props: { variant: "outlined", color: "primary" },
                    style: {
                        border: "1px solid rgba(255,255,255,0.25)",
                        color: "#e2e8f0",
                        "&:hover": {
                            border: "1px solid rgba(255,255,255,0.45)",
                            background: "rgba(255,255,255,0.06)",
                            backdropFilter: "blur(12px)",
                        },
                    },
                },
            ],
        },


    },
});
