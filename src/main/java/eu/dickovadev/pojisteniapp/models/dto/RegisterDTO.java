package eu.dickovadev.pojisteniapp.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dickovadev.pojisteniapp.validation.FieldsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@FieldsMatch(
        field = "password",
        confirmField = "confirmPassword",
        message = "Hesla se neshodují"
)
public class RegisterDTO {

    @NotBlank(message = "E-mail je povinný")
    @Email(message = "Neplatný formát e-mailu")
    @Size(max = 255, message = "E-mail je příliš dlouhý")
    private String email;

    @NotBlank(message = "Heslo je povinné")
    @Size(min = 10, max = 255, message = "Heslo musí mít alespoň 10 znaků")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Potvrzení hesla je povinné")
    @Size(min = 10, max = 255, message = "Heslo musí mít alespoň 10 znaků")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    //endregion

    @Override
    public String toString() {
        return "RegisterDTO{" +
                "email='" + email +
                '}';
    }
}
