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
import { api } from "../api/axios";
import { useFormHandler } from "../hooks/useFormHandler";
import type { RegisterRequest } from "../types/account";

type RegisterField = keyof RegisterRequest;
type FieldErrors = Partial<Record<RegisterField, string>>;

interface RegisterErrorResponse {
    type?: string;
    errors?: FieldErrors;
    field?: RegisterField;
    message?: string;
}

export default function RegisterPage() {
    const navigate = useNavigate();

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
    } = useFormHandler<RegisterRequest>({
        email: "",
        password: "",
        confirmPassword: "",
    });

    const canSubmit =
        !loading &&
        form.email.trim().length > 0 &&
        form.password.trim().length > 0 &&
        form.confirmPassword.trim().length > 0;

    const onFieldChange = (field: RegisterField) => (e: any) => {
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

        if (form.password !== form.confirmPassword) {
            setFieldErrors((prev) => ({
                ...prev,
                confirmPassword: "Heslo a potvrzení se neshodují.",
            }));
            setLoading(false);
            return;
        }

        try {
            await api.post<void>("/account/register", form);
            navigate("/login", { replace: true, state: { registered: true } });
        } catch (error: unknown) {
            const err = error as AxiosError<RegisterErrorResponse>;
            const status = err.response?.status;
            const data = err.response?.data;

            if (status === 400 && data?.type === "validation") {
                setFieldErrors(data.errors ?? {});
                return;
            }

            if (status === 409 && data?.field === "email") {
                setFieldErrors((prev) => ({
                    ...prev,
                    email: data.message ?? "Tento e-mail je již registrován.",
                }));
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
            title="Registrace"
            subtitle="Vytvořte si účet během chvilky."
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
                        autoComplete="new-password"
                    />

                    <TextField
                        label="Potvrzení hesla"
                        type="password"
                        value={form.confirmPassword}
                        onChange={onFieldChange("confirmPassword")}
                        error={Boolean(fieldErrors.confirmPassword)}
                        helperText={fieldErrors.confirmPassword}
                        autoComplete="new-password"
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        disabled={!canSubmit}
                        startIcon={loading ? <CircularProgress size={16} /> : undefined}
                    >
                        {loading ? "Registruji…" : "Registrovat se"}
                    </Button>

                    <Box sx={{ display: "flex", justifyContent: "center" }}>
                        <Button component={RouterLink} to="/login" variant="text" size="small">
                            Už máte účet? Přihlášení
                        </Button>
                    </Box>
                </Stack>
            </form>
        </FormWrapper>
    );
}
