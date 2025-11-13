export interface RegisterRequest {
    email: string;
    password: string;
    confirmPassword: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export type ChangePasswordRequest = {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
};
