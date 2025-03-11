package eu.dickovadev.pojisteniapp.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordDTO {

    @NotBlank(message = "Vyplňte původní heslo.")
    private String currentPassword;

    @NotBlank(message = "Vyplňte uživatelské heslo")
    @Size(min = 10, message = "Nové heslo musí mít alespoň 10 znaků.")
    private String newPassword;

    @NotBlank(message = "Vyplňte potvrzení hesla.")
    private String confirmPassword;

    // region: Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    // endregion

    @Override
    public String toString() {
        return "ChangePasswordDTO{" +
                "currentPassword='" + (currentPassword != null && !currentPassword.isEmpty() ? "[PROTECTED]" : "null") + '\'' +
                ", newPassword='" + (newPassword != null && !newPassword.isEmpty() ? "[PROTECTED]" : "null") + '\'' +
                ", confirmPassword='" + (confirmPassword != null && !confirmPassword.isEmpty() ? "[PROTECTED]" : "null") + '\'' +
                '}';
    }
}
