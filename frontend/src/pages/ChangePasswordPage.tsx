import type { FormEvent } from "react";
import type { AxiosError } from "axios";
import { Stack, TextField, Button, Alert, CircularProgress } from "@mui/material";

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

export default function ChangePasswordPage() {
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
        confirmPassword: "",
    });

    const canSubmit =
        !loading &&
        form.currentPassword.trim().length > 0 &&
        form.newPassword.trim().length > 0 &&
        form.confirmPassword.trim().length > 0;

    const onFieldChange = (field: ChangePasswordField) => (e: any) => {
        if (globalError) setGlobalError(null);
        if (successMessage) setSuccessMessage(null);

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

        if (form.newPassword !== form.confirmPassword) {
            setFieldErrors((prev) => ({
                ...prev,
                confirmPassword: "Nové heslo a potvrzení se neshodují.",
            }));
            setLoading(false);
            return;
        }

        try {
            await api.post<void>("/account/change-password", form);
            setSuccessMessage("Heslo bylo úspěšně změněno.");
        } catch (error: unknown) {
            const err = error as AxiosError<ChangePasswordErrorResponse>;
            const status = err.response?.status;
            const data = err.response?.data;

            if (status === 400 && data?.type === "validation") {
                setFieldErrors(data.errors ?? {});
                return;
            }

            if (status === 400 && data?.type === "business" && data.field) {
                const field = data.field;
                setFieldErrors((prev) => ({
                    ...prev,
                    [field]: data.message ?? "Zadané heslo není správně.",
                }));
                return;
            }

            if ((status === 401 || status === 403) && data?.type === "auth") {
                setGlobalError(
                    data.message ?? "Pro změnu hesla se prosím znovu přihlaste."
                );
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
            title="Změna hesla"
            subtitle="Zadejte původní heslo a nastavte nové."
            maxWidth={420}
        >
            <form onSubmit={handleSubmit} noValidate>
                <Stack spacing={3}>
                    {globalError && <Alert severity="error">{globalError}</Alert>}
                    {successMessage && <Alert severity="success">{successMessage}</Alert>}

                    <TextField
                        label="Původní heslo"
                        type="password"
                        value={form.currentPassword}
                        onChange={onFieldChange("currentPassword")}
                        error={Boolean(fieldErrors.currentPassword)}
                        helperText={fieldErrors.currentPassword}
                        autoComplete="current-password"
                    />

                    <TextField
                        label="Nové heslo"
                        type="password"
                        value={form.newPassword}
                        onChange={onFieldChange("newPassword")}
                        error={Boolean(fieldErrors.newPassword)}
                        helperText={fieldErrors.newPassword}
                        autoComplete="new-password"
                    />

                    <TextField
                        label="Potvrzení nového hesla"
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
                        {loading ? "Ukládám…" : "Změnit heslo"}
                    </Button>
                </Stack>
            </form>
        </FormWrapper>
    );
}
