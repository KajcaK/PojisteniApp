package eu.dickovadev.pojisteniapp.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyUserDTO {

    private long userId;

    @Email(message = "Vyplňte validní email")
    @NotBlank(message = "Vyplňte email")
    private String email;

    @NotBlank(message = "Vyplňte jméno")
    @Size(max = 50, message = "Jméno je příliš dlouhé")
    private String firstName;

    @NotBlank(message = "Vyplňte příjmení")
    @Size(max = 50, message = "Příjmení je příliš dlouhé")
    private String lastName;

    //region getters and setters
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    //endregion

    @Override
    public String toString() {
        return "PolicyUserDTO{" +
                "userId=" + userId +
                ", email=" + email +
                ", fullName='" + firstName + " " + lastName + '\'' +
                '}';
    }
}
