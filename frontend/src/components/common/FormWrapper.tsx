import type { ReactNode } from "react";
import { Box, Paper, Typography } from "@mui/material";

type FormWrapperProps = {
    children: ReactNode;
    title?: string;
    subtitle?: string;
    maxWidth?: number | string;
};

const ROOT_SX = {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    px: 2,
    background: `
        radial-gradient(circle at top, rgba(148, 163, 184, 0.25), transparent 55%),
        radial-gradient(circle at bottom, rgba(15, 23, 42, 0.9), #020617)
    `,
} as const;

const getPaperSx = (maxWidth: number | string) =>
    ({
        width: "100%",
        maxWidth,
        px: 4,
        py: 4.5,
        borderRadius: 3,
        background:
            "linear-gradient(135deg, rgba(15,23,42,0.75), rgba(15,23,42,0.35))",
        backdropFilter: "blur(22px) saturate(180%)",
        WebkitBackdropFilter: "blur(22px) saturate(180%)",
        border: "1px solid rgba(148,163,184,0.45)",
        boxShadow: "0 24px 80px rgba(0,0,0,0.85)",
    }) as const;

export function FormWrapper({
                                children,
                                title,
                                subtitle,
                                maxWidth = 420,
                            }: FormWrapperProps) {
    const hasHeader = Boolean(title || subtitle);

    return (
        <Box component="main" sx={ROOT_SX}>
            <Paper elevation={0} sx={getPaperSx(maxWidth)}>
                {hasHeader && (
                    <Box mb={3} textAlign="center">
                        {title && (
                            <Typography
                                variant="h6"
                                component="h1"
                                sx={{ fontWeight: 700, mb: subtitle ? 0.75 : 0 }}
                            >
                                {title}
                            </Typography>
                        )}
                        {subtitle && (
                            <Typography
                                variant="body2"
                                sx={{ color: "text.secondary" }}
                            >
                                {subtitle}
                            </Typography>
                        )}
                    </Box>
                )}

                {children}
            </Paper>
        </Box>
    );
}
