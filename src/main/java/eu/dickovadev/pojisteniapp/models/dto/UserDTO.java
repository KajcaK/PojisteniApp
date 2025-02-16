package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.models.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private long userId;

    @NotBlank(message = "Vyplňte jméno")
    @Size(min = 2, max = 50, message = "Jméno by mělo mít alespoň 2 znaky a maximálně 50 znaků")
    private String firstName;

    @NotBlank(message = "Vyplňte příjmení")
    @Size(min = 2, max = 50, message = "Příjmení by mělo mít alespoň 2 znaky a maximálně 50 znaků")
    private String lastName;

    @Email(message = "Vyplňte validní email")
    @NotBlank(message = "Vyplňte uživatelský email")
    private String email;

    @NotBlank(message = "Vyplňte telefon")
    @Pattern(regexp = "^\\+?([0-9]{1,3})?([0-9]{9})$", message = "Telefonní číslo musí mít správný formát")
    private String phone;

    @NotBlank(message = "Vyplňte ulici a číslo popisné")
    @Size(max = 100, message = "Adresa by neměla být delší než 100 znaků")
    private String street;

    @NotBlank(message = "Vyplňte město")
    @Size(max = 100, message = "Město by nemělo být delší než 100 znaků")
    private String city;


    @NotBlank(message = "Vyplňte PSČ")
    @Pattern(regexp = "^[0-9]{5}$", message = "PSČ musí mít 5 číslic")
    private String zipCode;

    private Set<Role> roles = new HashSet<>();

    private Set<PolicyDTO> policies;

    //region: getters and setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<PolicyDTO> getPolicies(){
        return policies;
    }

    public void setPolicies(Set<PolicyDTO> policies){
        this.policies = policies;
    }
    //endregion
}
