import { useState, type ChangeEvent } from "react";

export function useFormHandler<T extends Record<string, any>>(initialValues: T) {
    const [form, setForm] = useState<T>(initialValues);
    const [loading, setLoading] = useState(false);
    const [fieldErrors, setFieldErrors] =
        useState<Partial<Record<keyof T, string>>>({});
    const [globalError, setGlobalError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleChange =
        (field: keyof T) =>
            (event: ChangeEvent<HTMLInputElement>): void => {
                const value = event.target.value;

                setForm(prev => ({ ...prev, [field]: value }));
                setFieldErrors(prev => ({ ...prev, [field]: undefined }));
                setGlobalError(null);
                setSuccessMessage(null);
            };

    const resetMessages = (): void => {
        setGlobalError(null);
        setSuccessMessage(null);
        setFieldErrors({});
    };

    return {
        form,
        setForm,
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
    };
}
