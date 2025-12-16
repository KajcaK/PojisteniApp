import type { FormEvent, JSX } from "react";
import type { AxiosError } from "axios";
import { Stack, TextField, Button, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";

import { FormWrapper } from "../components/common/FormWrapper";
import { useAuth } from "../context/AuthContext";
import { useFormHandler } from "../hooks/useFormHandler";
import type { LoginRequest } from "../types/account";

type LoginField = keyof LoginRequest;
type FieldErrors = Partial<Record<LoginField, string>>;

interface LoginErrorResponse {
    type?: string;
    errors?: FieldErrors;
    message?: string;
}

function LoginPage(): JSX.Element {
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
        successMessage,
        setSuccessMessage,
        resetMessages,
    } = useFormHandler<LoginRequest>({
        email: "",
        password: "",
    });

    const handleSubmit = async (
        e: FormEvent<HTMLFormElement>
    ): Promise<void> => {
        e.preventDefault();
        if (loading) return;

        setLoading(true);
        resetMessages();

        try {
            // AuthContext handles API call + token, etc.
            await login(form.email, form.password);

            setSuccessMessage("Přihlášení proběhlo úspěšně.");
            // optional: small delay and then redirect, or just redirect
            navigate("/"); // or "/dashboard"
        } catch (error: unknown) {
            const err = error as AxiosError<LoginErrorResponse>;
            const status = err.response?.status;
            const data = err.response?.data;

            if (status === 400 && data?.type === "validation") {
                setFieldErrors(data.errors ?? {});
            } else if (status === 401) {
                setGlobalError(
                    data?.message || "Neplatný e-mail nebo heslo."
                );
            } else {
                setGlobalError("Něco se rozbilo. Zkuste to prosím znovu.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <FormWrapper title="Přihlášení">
            <form onSubmit={handleSubmit} noValidate>
                <Stack spacing={2.5}>
                    {globalError && (
                        <Alert severity="error">{globalError}</Alert>
                    )}

                    {successMessage && (
                        <Alert severity="success">{successMessage}</Alert>
                    )}

                    <TextField
                        label="Email"
                        type="email"
                        fullWidth
                        variant="outlined"
                        value={form.email}
                        onChange={handleChange("email")}
                        error={Boolean(fieldErrors.email)}
                        helperText={fieldErrors.email}
                        autoComplete="email"
                    />

                    <TextField
                        label="Heslo"
                        type="password"
                        fullWidth
                        variant="outlined"
                        value={form.password}
                        onChange={handleChange("password")}
                        error={Boolean(fieldErrors.password)}
                        helperText={fieldErrors.password}
                        autoComplete="current-password"
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        fullWidth
                        disabled={loading}
                    >
                        {loading ? "Přihlašuji..." : "Přihlásit se"}
                    </Button>
                </Stack>
            </form>
        </FormWrapper>
    );
}

export default LoginPage;
