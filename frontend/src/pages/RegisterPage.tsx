import React, { useState } from "react";
import { Stack, TextField, Button, Alert } from "@mui/material";
import { FormWrapper } from "../components/common/FormWrapper";
import { api } from "../api/axios";
import type { RegisterRequest } from "../types/account.ts";

type FieldErrors = Partial<Record<keyof RegisterRequest, string>>;

function RegisterPage() {
    const [form, setForm] = useState<RegisterRequest>({
        email: "",
        password: "",
        confirmPassword: "",
    });

    const [loading, setLoading] = useState(false);
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [globalError, setGlobalError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleChange =
        (field: keyof RegisterRequest) =>
            (event: React.ChangeEvent<HTMLInputElement>) => {
                const value = event.target.value;
                setForm(prev => ({ ...prev, [field]: value }));
                // Clear field-specific error as user types
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
            const response = await api.post("/account/register", form);

            // success
            setSuccessMessage("Účet úspěšně vytvořen. Můžete se přihlásit.");
            // later: redirect to /login
            // navigate("/login");
        } catch (err: any) {
            const status = err?.response?.status;
            const data = err?.response?.data;

            if (status === 400 && data?.type === "validation") {
                // shape from ApiExceptionHandler: { type: "validation", errors: { field: message } }
                setFieldErrors(data.errors ?? {});
            } else if (status === 409 && data?.field === "email") {
                // Duplicate email conflict
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
                    {globalError && <Alert severity="error">{globalError}</Alert>}
                    {successMessage && <Alert severity="success">{successMessage}</Alert>}

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

                    <TextField
                        label="Potvrzení hesla"
                        type="password"
                        fullWidth
                        variant="outlined"
                        value={form.confirmPassword}
                        onChange={handleChange("confirmPassword")}
                        error={Boolean(fieldErrors.confirmPassword)}
                        helperText={fieldErrors.confirmPassword}
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
