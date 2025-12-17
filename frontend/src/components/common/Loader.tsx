import { Box, CircularProgress } from "@mui/material";

type LoaderProps = {
    fullHeight?: boolean;
};

export function Loader({ fullHeight = false }: LoaderProps) {
    return (
        <Box
            sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                minHeight: fullHeight ? "100svh" : 240,
            }}
        >
            <CircularProgress
                size={28}
                thickness={4}
                color="inherit"
                sx={{ opacity: 0.6 }}
            />
        </Box>
    );
}
