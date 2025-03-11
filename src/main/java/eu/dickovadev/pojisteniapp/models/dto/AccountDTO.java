package eu.dickovadev.pojisteniapp.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountDTO {
    private long userId;

    @Email(message = "Vyplňte validní email")
    @NotBlank(message = "Vyplňte uživatelský email")
    private String email;

    @NotBlank(message = "Vyplňte uživatelské heslo")
    @Size(min = 10, message = "Heslo musí mít alespoň 10 znaků")
    private String password;

    @NotBlank(message = "Vyplňte uživatelské heslo")
    @Size(min = 10, message = "Heslo musí mít alespoň 10 znaků")
    private String confirmedPassword;

    //region: getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    public long getUserId() {
        return userId;
    }
    //endregion

    @Override
    public String toString() {
        return "AccountDTO{" +
                "userId=" + userId +
                ", email='" + email +
                '}';
    }
}
