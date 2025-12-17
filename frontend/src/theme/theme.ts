import { alpha, createTheme, darken } from "@mui/material/styles";

export const theme = createTheme({
    palette: {
        mode: "dark",

        primary: { main: "#56B8DB" },
        secondary: { main: "#7F76E8" },

        background: {
            default: "#050A14",
            paper: alpha("#0B1220", 0.72),
        },

        text: {
            primary: "#E6EAF0",
            secondary: "#A8B0C2",
            disabled: "#6B7280",
        },

        error: { main: "#E0565B" },
        warning: { main: "#D6A34A" },
        success: { main: "#3FB98A" },
        info: { main: "#4C8ED9" },

        divider: alpha("#7F76E8", 0.28),

        action: {
            hover: alpha("#7F76E8", 0.10),
            selected: alpha("#56B8DB", 0.14),
            disabled: alpha("#A8B0C2", 0.38),
            disabledBackground: alpha("#0B1220", 0.6)
        },
    },

    typography: {
        fontFamily: "'Inter', system-ui, -apple-system, BlinkMacSystemFont, sans-serif",

        // Display headings
        h1: {
            fontFamily: "'Space Grotesk', Inter, sans-serif",
            fontWeight: 600,
            letterSpacing: "-0.02em",
        },
        h2: {
            fontFamily: "'Space Grotesk', Inter, sans-serif",
            fontWeight: 600,
            letterSpacing: "-0.02em",
        },
        h3: {
            fontFamily: "'Space Grotesk', Inter, sans-serif",
            fontWeight: 600,
            letterSpacing: "-0.015em",
        },
        h4: {
            fontFamily: "'Space Grotesk', Inter, sans-serif",
            fontWeight: 600,
            letterSpacing: "-0.01em",
        },

        // Utility headings
        h5: {
            fontFamily: "'Inter', sans-serif",
            fontWeight: 600,
            letterSpacing: "-0.01em",
        },
        h6: {
            fontFamily: "'Inter', sans-serif",
            fontWeight: 600,
            letterSpacing: "0",
        },

        // Body text
        body1: {
            fontSize: "1rem",
            lineHeight: 1.6,
        },
        body2: {
            fontSize: "0.875rem",
            lineHeight: 1.6,
        },

        // Supporting text
        subtitle1: {
            fontSize: "0.875rem",
            fontWeight: 500,
            lineHeight: 1.5,
            color: "text.secondary",
        },

        caption: {
            fontSize: "0.75rem",
            lineHeight: 1.4,
            color: "text.secondary",
        },
    },

    shape: { borderRadius: 16 },

    components: {
        MuiAppBar: {
            defaultProps: {
                position: "fixed",
                color: "transparent",
                elevation: 0,
            },
            styleOverrides: {
                root: ({ theme }) => ({
                    zIndex: theme.zIndex.drawer + 1,
                    backgroundColor: alpha(theme.palette.background.paper, 0.55),
                    backdropFilter: "blur(16px) saturate(160%)",
                    WebkitBackdropFilter: "blur(16px) saturate(160%)",
                    borderBottom: `1px solid ${alpha(theme.palette.secondary.main, 0.22)}`,
                    boxShadow: `0 8px 32px ${alpha("#000", 0.35)}`,
                    transition: "background-color 200ms ease, box-shadow 200ms ease",
                }),
            },
        },

        MuiPaper: {
            defaultProps: { elevation: 0 },
            styleOverrides: {
                root: ({ theme }) => ({
                    backgroundImage: "none",
                    backgroundColor: theme.palette.background.paper,
                    borderRadius: theme.shape.borderRadius,
                }),
            },
            variants: [
                {
                    props: { variant: "glass" },
                    style: ({ theme }) => ({
                        backgroundImage: `
                          linear-gradient(
                            180deg,
                            ${alpha("#0B1220", 0.78)} 0%,
                            ${alpha("#050A14", 0.62)} 100%
                          )
                        `,
                        backgroundColor: "transparent",
                        border: `1px solid ${alpha(theme.palette.secondary.main, 0.20)}`,
                        borderRadius: 3 * theme.shape.borderRadius,
                        backdropFilter: "blur(20px) saturate(160%)",
                        WebkitBackdropFilter: "blur(20px) saturate(160%)",
                        boxShadow: `0 24px 80px ${alpha("#000", 0.55)}`,
                        overflow: "hidden",
                        padding: theme.spacing(3),
                        [theme.breakpoints.up("sm")]: {
                            padding: theme.spacing(4),
                        },
                    }),
                },
            ],
        },

        MuiCard: {
            defaultProps: { elevation: 0 },
            styleOverrides: {
                root: ({ theme }) => ({
                    backgroundImage: "none",
                    backgroundColor: theme.palette.background.paper,
                    border: `1px solid ${alpha(theme.palette.secondary.main, 0.18)}`,
                    borderRadius: theme.shape.borderRadius,
                    boxShadow: `0 10px 32px ${alpha("#000", 0.35)}`,
                    backdropFilter: "none",
                    WebkitBackdropFilter: "none",

                    overflow: "hidden",
                }),
            },
        },

        MuiTextField: {
            defaultProps: { fullWidth: true, size: "small", variant: "outlined" },
        },

        MuiOutlinedInput: {
            styleOverrides: {
                root: ({ theme }) => ({
                    borderRadius: theme.shape.borderRadius,
                    backgroundColor: alpha(theme.palette.background.paper, 0.55),
                    transition: "background-color 150ms ease, box-shadow 150ms ease",

                    "&:hover": {
                        backgroundColor: alpha(theme.palette.background.paper, 0.7),
                    },

                    "&.Mui-focused": {
                        boxShadow: `0 0 0 3px ${alpha(theme.palette.primary.main, 0.22)}`,
                    },

                    "&.Mui-error": {
                        boxShadow: `0 0 0 3px ${alpha(theme.palette.error.main, 0.20)}`,
                    },

                    "& .MuiOutlinedInput-notchedOutline": {
                        borderColor: alpha(theme.palette.secondary.main, 0.22),
                    },
                    "&:hover .MuiOutlinedInput-notchedOutline": {
                        borderColor: alpha(theme.palette.secondary.main, 0.38),
                    },
                    "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                        borderColor: alpha(theme.palette.primary.main, 0.65),
                    },
                    "&.Mui-error .MuiOutlinedInput-notchedOutline": {
                        borderColor: alpha(theme.palette.error.main, 0.65),
                    },
                }),

                input: ({ theme }) => ({
                    color: theme.palette.text.primary,
                    paddingTop: 10,
                    paddingBottom: 10,

                    "&::placeholder": {
                        color: alpha(theme.palette.text.secondary, 0.9),
                        opacity: 1,
                    },
                }),
            },
        },

        MuiInputLabel: {
            styleOverrides: {
                root: ({ theme }) => ({
                    color: theme.palette.text.secondary,

                    "&.Mui-focused": {
                        color: theme.palette.primary.main,
                    },
                    "&.Mui-error": {
                        color: theme.palette.error.main,
                    },
                }),
            },
        },

        MuiFormHelperText: {
            styleOverrides: {
                root: ({ theme }) => ({
                    marginLeft: 0,
                    marginRight: 0,
                    marginTop: theme.spacing(0.75),
                    color: theme.palette.text.secondary,

                    "&.Mui-error": {
                        color: theme.palette.error.main,
                    },
                }),
            },
        },

        MuiButton: {
            defaultProps: { disableElevation: true },
            styleOverrides: {
                root: ({ theme }) => ({
                    borderRadius: 999,
                    textTransform: "none",
                    fontWeight: 500,
                    paddingLeft: 20,
                    paddingRight: 20,
                    paddingTop: 8,
                    paddingBottom: 8,
                    transition:
                        "transform 0.2s ease, background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease",

                    "&:hover": { transform: "translateY(-1px)" },

                    "@media (prefers-reduced-motion: reduce)": {
                        transition: "none",
                        "&:hover": { transform: "none" },
                    },

                    "&[aria-current='page']": {
                        backgroundColor: theme.palette.action.selected,
                    },
                }),
            },

            variants: [
                // Primary contained (main CTA)
                {
                    props: { variant: "contained", color: "primary" },
                    style: ({ theme }) => ({
                        color: theme.palette.text.primary,

                        border: `1px solid ${alpha(theme.palette.primary.main, 0.35)}`,

                        backgroundImage: `
          linear-gradient(135deg,
            ${alpha(theme.palette.primary.main, 0.22)} 0%,
            ${alpha(theme.palette.secondary.main, 0.10)} 55%,
            ${alpha("#000", 0.35)} 100%
          )
        `,
                        backgroundColor: alpha(theme.palette.background.paper, 0.35),

                        boxShadow: `0 10px 32px ${alpha("#000", 0.40)}`,

                        "&:hover": {
                            border: `1px solid ${alpha(theme.palette.primary.main, 0.55)}`,
                            backgroundImage: `
            linear-gradient(135deg,
              ${alpha(theme.palette.primary.main, 0.28)} 0%,
              ${alpha(theme.palette.secondary.main, 0.12)} 55%,
              ${alpha("#000", 0.40)} 100%
            )
          `,
                            boxShadow: `0 14px 44px ${alpha("#000", 0.50)}`,
                        },

                        "&:active": {
                            transform: "translateY(0px)",
                            boxShadow: `0 8px 24px ${alpha("#000", 0.45)}`,
                        },
                    }),
                },

                // Text primary (links / subtle actions)
                {
                    props: { variant: "text", color: "primary" },
                    style: ({ theme }) => ({
                        color: theme.palette.text.primary,
                        "&:hover": {
                            backgroundColor: theme.palette.action.hover,
                        },
                    }),
                },

                // Outlined primary (secondary actions)
                {
                    props: { variant: "outlined", color: "primary" },
                    style: ({ theme }) => ({
                        color: theme.palette.text.primary,
                        border: `1px solid ${alpha(theme.palette.secondary.main, 0.28)}`,
                        backgroundColor: "transparent",

                        "&:hover": {
                            border: `1px solid ${alpha(theme.palette.secondary.main, 0.45)}`,
                            backgroundColor: alpha(theme.palette.secondary.main, 0.10),
                        },
                    }),
                },
            ],
        },

        MuiAlert: {
            defaultProps: { variant: "standard" },
            styleOverrides: {
                root: ({ theme }) => ({
                    backgroundColor: alpha(theme.palette.background.paper, 0.55),
                    backdropFilter: "blur(14px) saturate(150%)",
                    WebkitBackdropFilter: "blur(14px) saturate(150%)",

                    borderRadius: theme.shape.borderRadius,
                    border: `1px solid ${alpha(theme.palette.secondary.main, 0.18)}`,

                    color: theme.palette.text.primary,
                    alignItems: "center",
                    boxShadow: `0 10px 32px ${alpha("#000", 0.35)}`,
                }),

                icon: ({ theme }) => ({
                    opacity: 0.95,
                    color: "inherit",
                }),

                message: ({ theme }) => ({
                    padding: `${theme.spacing(0.75)} 0`,
                }),

                standard: ({ theme }) => ({
                    "&.MuiAlert-colorSuccess": {
                        borderColor: alpha(theme.palette.success.main, 0.45),
                        backgroundColor: alpha(theme.palette.success.main, 0.10),
                    },
                    "&.MuiAlert-colorError": {
                        borderColor: alpha(theme.palette.error.main, 0.45),
                        backgroundColor: alpha(theme.palette.error.main, 0.10),
                    },
                    "&.MuiAlert-colorWarning": {
                        borderColor: alpha(theme.palette.warning.main, 0.45),
                        backgroundColor: alpha(theme.palette.warning.main, 0.10),
                    },
                    "&.MuiAlert-colorInfo": {
                        borderColor: alpha(theme.palette.info.main, 0.45),
                        backgroundColor: alpha(theme.palette.info.main, 0.10),
                    },
                }),
            },
        },

        MuiCssBaseline: {
            styleOverrides: {
                body: ({ theme }) => ({
                    margin: 0,
                    minHeight: "100vh",
                    color: "inherit",
                    backgroundColor: theme.palette.background.default,

                    backgroundImage: `
        radial-gradient(1200px 700px at 18% 12%, ${alpha(theme.palette.primary.main, 0.10)} 0%, transparent 60%),
        radial-gradient(1000px 650px at 82% 28%, ${alpha(theme.palette.secondary.main, 0.08)} 0%, transparent 62%),
        linear-gradient(180deg,
          ${theme.palette.background.default} 0%,
          ${darken(theme.palette.background.default, 0.10)} 55%,
          ${theme.palette.background.default} 100%
        )
      `,
                    backgroundAttachment: "fixed",

                    "@media (pointer: coarse)": { backgroundAttachment: "scroll" },

                    "*": {
                        scrollbarWidth: "thin",
                        scrollbarColor: `${alpha(theme.palette.text.secondary, 0.35)} transparent`,
                    },
                    "::-webkit-scrollbar": { width: 8 },
                    "::-webkit-scrollbar-thumb": {
                        backgroundColor: alpha(theme.palette.text.secondary, 0.28),
                        borderRadius: 8,
                    },
                    "::-webkit-scrollbar-thumb:hover": {
                        backgroundColor: alpha(theme.palette.text.secondary, 0.42),
                    },

                    "::selection": {
                        backgroundColor: alpha(theme.palette.primary.main, 0.28),
                    },
                }),
                "#root": { minHeight: "100vh" },
            },
        },

    },
});

