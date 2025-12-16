import type { FormEvent, JSX } from "react";
import type { AxiosError } from "axios";
import { Stack, TextField, Button, Alert } from "@mui/material";

import { FormWrapper } from "../components/common/FormWrapper";
import { api } from "../api/axios";
import { useFormHandler } from "../hooks/useFormHandler";
import type { ChangePasswordRequest } from "../types/account";

type ChangePasswordField = keyof ChangePasswordRequest;
type FieldErrors = Partial<Record<ChangePasswordField, string>>;

interface ChangePasswordErrorResponse {
    type?: string;
    errors?: FieldErrors;
    field?: ChangePasswordField;
    message?: string;
}

function ChangePasswordPage(): JSX.Element {
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
    } = useFormHandler<ChangePasswordRequest>({
        currentPassword: "",
        newPassword: "",
        confirmNewPassword: "",
    });

    const handleSubmit = async (
        e: FormEvent<HTMLFormElement>
    ): Promise<void> => {
        e.preventDefault();
        if (loading) return;

        setLoading(true);
        resetMessages();

        // simple client-side check before hitting API
        if (form.newPassword !== form.confirmNewPassword) {
            setFieldErrors(prev => ({
                ...prev,
                confirmNewPassword: "Nové heslo a potvrzení se neshodují.",
            }));
            setLoading(false);
            return;
        }

        try {
            await api.post<void>("/account/change-password", form);

            setSuccessMessage("Heslo bylo úspěšně změněno.");
            // optional: reset form later if you want
            // setForm({ currentPassword: "", newPassword: "", confirmNewPassword: "" });
        } catch (error: unknown) {
            const err = error as AxiosError<ChangePasswordErrorResponse>;
            const status = err.response?.status;
            const data = err.response?.data;

            if (status === 400 && data?.type === "validation") {
                // { type: "validation", errors: { field: message } }
                setFieldErrors(data.errors ?? {});
            } else if (status === 400 && data?.type === "business" && data.field) {
                const field = data.field as ChangePasswordField;

                setFieldErrors(prev => ({
                    ...prev,
                    [field]: data.message || "Zadané heslo není správně.",
                }));
            } else if (
                (status === 401 || status === 403) &&
                data?.type === "auth"
            ) {
                setGlobalError(
                    data.message ||
                    "Pro změnu hesla se prosím znovu přihlaste."
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
                        value={form.confirmNewPassword}
                        onChange={handleChange("confirmNewPassword")}
                        error={Boolean(fieldErrors.confirmNewPassword)}
                        helperText={fieldErrors.confirmNewPassword}
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
