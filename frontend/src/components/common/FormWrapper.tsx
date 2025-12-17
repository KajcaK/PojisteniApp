import type { ReactNode } from "react";
import { Box, Paper, Stack, Typography } from "@mui/material";

type FormWrapperProps = {
    children: ReactNode;
    title?: string;
    subtitle?: string;
    maxWidth?: number | string;
    centered?: boolean;
};

export function FormWrapper({
                                children,
                                title,
                                subtitle,
                                maxWidth = 420,
                                centered = false,
                            }: FormWrapperProps) {
    const hasHeader = Boolean(title || subtitle);

    return (
        <Box
            sx={
                centered
                    ? {
                        display: "flex",
                        justifyContent: "center",
                        py: { xs: 2, sm: 4 },
                    }
                    : undefined
            }
        >
            <Paper variant="glass" sx={{ width: "100%", maxWidth }}>
                {hasHeader && (
                    <Stack spacing={1} sx={{ mb: 4, textAlign: "center" }}>
                        {title && (
                            <Typography variant="h3" component="h1">
                                {title}
                            </Typography>
                        )}
                        {subtitle && <Typography variant="subtitle1">{subtitle}</Typography>}
                    </Stack>
                )}

                {children}
            </Paper>
        </Box>
    );
}
