package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = mock(UserService.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    AuditLogService auditLogService = mock(AuditLogService.class);

    AccountServiceImpl service;

    @BeforeEach
    void setup() {
        service = new AccountServiceImpl(userRepository, passwordEncoder, auditLogService, userService);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(UserEntity principal) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        var ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void changePassword_happyPath_savesAndLogs() {
        // given
        Long userId = 1L;
        UserEntity principal = new UserEntity();
        principal.setUserId(userId);
        authenticateAs(principal);

        UserEntity persisted = new UserEntity();
        persisted.setUserId(userId);
        persisted.setPassword("$enc_old");
        when(userService.getEntityByIdOrThrow(userId)).thenReturn(persisted);
        when(passwordEncoder.matches("oldpass", "$enc_old")).thenReturn(true);
        when(passwordEncoder.encode("newpass123")).thenReturn("$enc_new");

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldpass");
        dto.setNewPassword("newpass123");
        dto.setConfirmPassword("newpass123");

        // when
        service.changePassword(userId, dto);

        // then
        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(saved.capture());
        verify(auditLogService).logAction(eq("UPDATE"), eq("UserEntity"), eq(userId), contains("Password updated"));

        // password encoded
        verify(passwordEncoder).encode("newpass123");
    }

    @Test
    void changePassword_wrongCurrent_throwsInvalidPassword() {
        Long userId = 1L;
        UserEntity principal = new UserEntity(); principal.setUserId(userId);
        authenticateAs(principal);

        UserEntity persisted = new UserEntity();
        persisted.setUserId(userId);
        persisted.setPassword("$enc_old");
        when(userService.getEntityByIdOrThrow(userId)).thenReturn(persisted);
        when(passwordEncoder.matches("WRONG", "$enc_old")).thenReturn(false);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("WRONG");
        dto.setNewPassword("newpassword1");
        dto.setConfirmPassword("newpassword1");

        assertThatThrownBy(() -> service.changePassword(userId, dto))
                .isInstanceOf(InvalidPasswordException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_otherUser_throwsAccessDenied() {
        Long userId = 1L;
        UserEntity principal = new UserEntity(); principal.setUserId(99L);
        authenticateAs(principal);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldpass");
        dto.setNewPassword("newpassword1");
        dto.setConfirmPassword("newpassword1");

        assertThatThrownBy(() -> service.changePassword(userId, dto))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(userService, userRepository, passwordEncoder, auditLogService);
    }
}
