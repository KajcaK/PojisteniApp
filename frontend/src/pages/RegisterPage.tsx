import type { FormEvent, JSX } from "react";
import type { AxiosError } from "axios";
import { Stack, TextField, Button, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";

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

function RegisterPage(): JSX.Element {
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
        successMessage,
        setSuccessMessage,
        resetMessages,
    } = useFormHandler<RegisterRequest>({
        email: "",
        password: "",
        confirmPassword: "",
    });

    const handleSubmit = async (
        e: FormEvent<HTMLFormElement>
    ): Promise<void> => {
        e.preventDefault();
        if (loading) return;

        setLoading(true);
        resetMessages();

        try {
            await api.post<void>("/account/register", form);

            setSuccessMessage("Účet úspěšně vytvořen. Můžete se přihlásit.");
            navigate("/login");
        } catch (error: unknown) {
            const err = error as AxiosError<RegisterErrorResponse>;
            const status = err.response?.status;
            const data = err.response?.data;

            if (status === 400 && data?.type === "validation") {
                setFieldErrors(data.errors ?? {});
            } else if (status === 409 && data?.field === "email") {
                setFieldErrors(prev => ({
                    ...prev,
                    email: data.message || "Tento e-mail je již registrován.",
                }));
            } else {
                setGlobalError("Něco se rozbilo. Zkuste to prosím znovu.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <FormWrapper title="Registrace">
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
                        autoComplete="new-password"
                    />

                    <TextField
                        label="Potvrzení hesla"
                        type="password"
                        fullWidth
                        variant="outlined"
                        value={form.confirmPassword}
                        onChange={handleChange("confirmPassword")}
                        error={Boolean(fieldErrors.confirmPassword)}
                        helperText={fieldErrors.confirmPassword}
                        autoComplete="new-password"
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        fullWidth
                        disabled={loading}
                    >
                        {loading ? "Registruji..." : "Registrovat se"}
                    </Button>
                </Stack>
            </form>
        </FormWrapper>
    );
}

export default RegisterPage;
