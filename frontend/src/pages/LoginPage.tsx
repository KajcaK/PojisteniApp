import React, { useState } from "react";
import { Stack, TextField, Button, Alert } from "@mui/material";
import { FormWrapper } from "../components/common/FormWrapper";
import { api } from "../api/axios";
import type { LoginRequest } from "../types/account.ts";

type FieldErrors = Partial<Record<keyof LoginRequest, string>>;

function LoginPage() {
    const [form, setForm] = useState<LoginRequest>({
        email: "",
        password: "",
    });

    const [loading, setLoading] = useState(false);
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [globalError, setGlobalError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleChange =
        (field: keyof LoginRequest) =>
            (event: React.ChangeEvent<HTMLInputElement>) => {
                const value = event.target.value;
                setForm(prev => ({ ...prev, [field]: value }));
                // clear field-specific error & global messages when user edits
                setFieldErrors(prev => ({ ...prev, [field]: undefined }));
                setGlobalError(null);
                setSuccessMessage(null);
            };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setGlobalError(null);
        setFieldErrors({});
        setSuccessMessage(null);

        try {
            const response = await api.post("/account/login", form);

            // TODO: handle token/user from response once backend is ready
            // e.g. save JWT, set auth context, redirect, etc.
            // For now just show a success message:
            setSuccessMessage("Přihlášení proběhlo úspěšně.");

            // later:
            // authContext.login(response.data);
            // navigate("/dashboard");
        } catch (err: any) {
            const status = err?.response?.status;
            const data = err?.response?.data;

            if (status === 400 && data?.type === "validation") {
                // shape from ApiExceptionHandler:
                // { type: "validation", errors: { field: message } }
                setFieldErrors(data.errors ?? {});
            } else if (status === 401) {
                // invalid credentials
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
