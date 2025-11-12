package eu.dickovadev.pojisteniapp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dickovadev.pojisteniapp.validation.FieldsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldsMatch(
        field = "newPassword",
        confirmField = "confirmPassword",
        message = "Hesla se neshodují"
)
public class ChangePasswordDTO {

    @NotBlank(message = "Vyplňte původní heslo.")
    @Size(min = 10, max = 255, message = "Původní heslo musí mít alespoň 10 znaků.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;

    @NotBlank(message = "Vyplňte nové heslo.")
    @Size(min = 10, max = 255, message = "Nové heslo musí mít alespoň 10 znaků.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    @NotBlank(message = "Vyplňte potvrzení hesla.")
    @Size(min = 10, max = 255, message = "Potvrzení hesla musí mít alespoň 10 znaků.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    // region: Getters and Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    // endregion

    @Override
    public String toString() {
        return "ChangePasswordDTO{currentPassword='***', newPassword='***', confirmPassword='***'}";
    }
}
