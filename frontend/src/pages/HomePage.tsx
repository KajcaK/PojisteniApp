import {Stack, Typography} from "@mui/material";

export default function HomePage() {
  return (
      <Stack spacing={2}>
          <Typography variant="h3" component="h1">
              Home
          </Typography>

          <Typography variant="body1" color="text.secondary">
              Placeholder
          </Typography>
      </Stack>

  );
}

