package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.testsupport.ValidatorTestBase;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordDTOTest extends ValidatorTestBase {

    @Test
    void validPayload_passesValidation() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldpassword1");
        dto.setNewPassword("newpassword1");
        dto.setConfirmPassword("newpassword1");

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void mismatchedPasswords_triggersErrorOnConfirmField() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldpassword1");
        dto.setNewPassword("newpassword1");
        dto.setConfirmPassword("differentpassword");

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(v -> {
                    assertThat(v.getPropertyPath().toString()).isEqualTo("confirmPassword");
                    assertThat(v.getMessage()).contains("Hesla se neshodují");
                });
    }

    @Test
    void shortNewPassword_triggersSizeViolation() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldpassword1");
        dto.setNewPassword("short");
        dto.setConfirmPassword("short");

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("newPassword"));
    }

    @Test
    void blankFields_triggerNotBlankViolations() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("");
        dto.setNewPassword("");
        dto.setConfirmPassword("");

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);

        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains("currentPassword", "newPassword", "confirmPassword");
    }

    @Test
    void whitespaceOnlyPassword_isInvalid() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("   ");
        dto.setNewPassword("   ");
        dto.setConfirmPassword("   ");

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);

        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains("currentPassword", "newPassword", "confirmPassword");
    }

    @Test
    void confirmMissing_triggersFieldMatchViolation() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldpassword1");
        dto.setNewPassword("newpassword1");
        dto.setConfirmPassword(null);

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);

        // Should still complain because confirm doesn't match newPassword
        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains("confirmPassword");
    }
}
