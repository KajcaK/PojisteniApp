import type { FormEvent } from "react";
import type { AxiosError } from "axios";
import {
    Stack,
    TextField,
    Button,
    Alert,
    CircularProgress,
    Box,
} from "@mui/material";
import { useNavigate, Link as RouterLink } from "react-router-dom";

import { FormWrapper } from "../components/common/FormWrapper";
import { useAuth } from "../context/useAuth";
import { useFormHandler } from "../hooks/useFormHandler";
import type { LoginRequest } from "../types/account";

type LoginField = keyof LoginRequest;
type FieldErrors = Partial<Record<LoginField, string>>;

interface LoginErrorResponse {
    type?: string;
    errors?: FieldErrors;
    message?: string;
}

export default function LoginPage() {
    const navigate = useNavigate();
    const { login } = useAuth();

    const {
        form,
        handleChange,
        loading,
        setLoading,
        fieldErrors,
        setFieldErrors,
        globalError,
        setGlobalError,
        resetMessages,
    } = useFormHandler<LoginRequest>({
        email: "",
        password: "",
    });

    const canSubmit =
        !loading &&
        form.email.trim().length > 0 &&
        form.password.trim().length > 0;

    const onFieldChange = (field: LoginField) => (e: any) => {
        if (globalError) setGlobalError(null);
        if (fieldErrors[field]) {
            setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
        }
        handleChange(field)(e);
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (loading) return;

        setLoading(true);
        resetMessages();

        try {
            await login(form.email, form.password);
            navigate("/", { replace: true });
        } catch (error: unknown) {
            const err = error as AxiosError<LoginErrorResponse>;
            const status = err.response?.status;
            const data = err.response?.data;

            if (status === 400 && data?.type === "validation") {
                setFieldErrors(data.errors ?? {});
                return;
            }

            if (status === 401) {
                setGlobalError(data?.message ?? "Neplatný e-mail nebo heslo.");
                return;
            }

            setGlobalError("Něco se rozbilo. Zkuste to prosím znovu.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <FormWrapper
            centered
            title="Přihlášení"
            subtitle="Použijte svůj e-mail a heslo."
            maxWidth={420}
        >
            <form onSubmit={handleSubmit} noValidate>
                <Stack spacing={3}>
                    {globalError && <Alert severity="error">{globalError}</Alert>}

                    <TextField
                        label="Email"
                        type="email"
                        value={form.email}
                        onChange={onFieldChange("email")}
                        error={Boolean(fieldErrors.email)}
                        helperText={fieldErrors.email}
                        autoComplete="email"
                    />

                    <TextField
                        label="Heslo"
                        type="password"
                        value={form.password}
                        onChange={onFieldChange("password")}
                        error={Boolean(fieldErrors.password)}
                        helperText={fieldErrors.password}
                        autoComplete="current-password"
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        disabled={!canSubmit}
                        startIcon={loading ? <CircularProgress size={16} /> : undefined}
                    >
                        {loading ? "Přihlašuji…" : "Přihlásit se"}
                    </Button>

                    <Box sx={{ display: "flex", justifyContent: "center" }}>
                        <Button component={RouterLink} to="/register" variant="text" size="small">
                            Nemáte účet? Registrace
                        </Button>
                    </Box>
                </Stack>
            </form>
        </FormWrapper>
    );
}
