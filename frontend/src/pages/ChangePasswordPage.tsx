import React, { useState } from "react";
import { Stack, TextField, Button, Alert } from "@mui/material";
import { FormWrapper } from "../components/common/FormWrapper";
import { api } from "../api/axios";
import type { ChangePasswordRequest } from "../types/account.ts";

type FieldErrors = Partial<Record<keyof ChangePasswordRequest, string>>;

function ChangePasswordPage() {
    const [form, setForm] = useState<ChangePasswordRequest>({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    });

    const [loading, setLoading] = useState(false);
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [globalError, setGlobalError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleChange =
        (field: keyof ChangePasswordRequest) =>
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
        if (loading) return;

        setLoading(true);
        setGlobalError(null);
        setFieldErrors({});
        setSuccessMessage(null);

        // @ts-ignore
        try {
            const response = await api.post("/account/change-password", form);

            if (response.status === 200) {
                setSuccessMessage("Heslo bylo úspěšně změněno.");
                // optional later: reset form, redirect, etc.
                // setForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
            } else {
                setGlobalError("Něco se rozbilo. Zkuste to prosím znovu.");
            }
        } catch (err: any) {
            const status = err?.response?.status;
            const data = err?.response?.data;

            if (status === 400 && data?.type === "validation") {
                // { type: "validation", errors: { field: message } }
                setFieldErrors(data.errors ?? {});
            } else if (status === 400 && data?.type === "business" && data?.field) {
                // e.g. invalid current password mapped to currentPassword
                setFieldErrors(prev => ({
                    ...prev,
                    [data.field]: data.message || "Zadané heslo není správně.",
                }));
            } else if ((status === 401 || status === 403) && data?.type === "auth") {
                // not logged in / no permission
                setGlobalError(
                    data?.message || "Pro změnu hesla se prosím znovu přihlaste."
                );
            } else {
                setGlobalError("Něco se rozbilo. Zkuste to prosím znovu.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <FormWrapper title="Změna hesla">
            <form onSubmit={handleSubmit} noValidate>
                <Stack spacing={2.5}>
                    {globalError && <Alert severity="error">{globalError}</Alert>}
                    {successMessage && (
                        <Alert severity="success">{successMessage}</Alert>
                    )}

                    <TextField
                        label="Původní heslo"
                        type="password"
                        fullWidth
                        variant="outlined"
                        value={form.currentPassword}
                        onChange={handleChange("currentPassword")}
                        error={Boolean(fieldErrors.currentPassword)}
                        helperText={fieldErrors.currentPassword}
                        autoComplete="current-password"
                    />

                    <TextField
                        label="Nové heslo"
                        type="password"
                        fullWidth
                        variant="outlined"
                        value={form.newPassword}
                        onChange={handleChange("newPassword")}
                        error={Boolean(fieldErrors.newPassword)}
                        helperText={fieldErrors.newPassword}
                        autoComplete="new-password"
                    />

                    <TextField
                        label="Potvrzení nového hesla"
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
                        {loading ? "Ukládám..." : "Změnit heslo"}
                    </Button>
                </Stack>
            </form>
        </FormWrapper>
    );
}

export default ChangePasswordPage;
