package eu.dickovadev.pojisteniapp.models.dto;

public class PolicyUserDTO {

    private long userId;
    private String email;
    private String firstName;
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
