package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.testsupport.ValidatorTestBase;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginDTOTest extends ValidatorTestBase {

    @Test
    void validPayload_passesValidation() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("abcdefghij"); // 10 chars

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shortPassword_triggersErrorOnPasswordField() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("abcdefg"); // 7 chars

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "user@", "@example.com", "user@@example.com", "user example.com"})
    void invalidEmail_formatsTriggerErrorOnEmailField(String badEmail) {
        LoginDTO dto = new LoginDTO();
        dto.setEmail(badEmail);
        dto.setPassword("abcdefghij");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("email"));
    }

    @Test
    void blankFields_triggerNotBlankErrors() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("");    // @NotBlank should fail
        dto.setPassword(""); // @NotBlank should fail

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains("email", "password");
    }

    @Test
    void overlyLongEmail_triggersSizeMaxOnEmail() {
        String longLocal = "a".repeat(260);
        LoginDTO dto = new LoginDTO();
        dto.setEmail(longLocal + "@example.com"); // > 255 total likely
        dto.setPassword("abcdefghij");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("email"));
    }

    @Test
    void whitespaceOnlyPassword_isNotBlankViolation() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("   "); // NotBlank should treat this as invalid

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("password"));
    }
}
