package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.testsupport.ValidatorTestBase;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterDTOTest extends ValidatorTestBase {

    @Test
    void validPayload_passesValidation() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("abcdefghij");
        dto.setConfirmPassword("abcdefghij");

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void mismatchedPasswords_triggersErrorOnConfirm() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("abcdefghij");
        dto.setConfirmPassword("xxxxxxxxxx");

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("confirmPassword");
            assertThat(v.getMessage()).contains("Hesla se neshodují");
        });
    }

    @Test
    void blankFields_triggerNotBlankErrors() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("");
        dto.setPassword("");
        dto.setConfirmPassword("");

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains("email", "password", "confirmPassword");
    }
}
