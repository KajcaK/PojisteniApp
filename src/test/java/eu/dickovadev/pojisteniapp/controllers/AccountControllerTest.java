package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.dto.RegisterDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
import eu.dickovadev.pojisteniapp.services.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    private MockMvc mockMvc;
    private AccountService accountService;

    private static UserEntity user(long id) {
        UserEntity u = new UserEntity();
        try {
            var f = UserEntity.class.getDeclaredField("userId");
            f.setAccessible(true);
            f.set(u, id);
        } catch (Exception ignored) {}
        return u;
    }

    private void asUser(long id) {
        var auth = new TestingAuthenticationToken(user(id), null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setup() {
        accountService = mock(AccountService.class);
        var controller = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /account/login renders view and title")
    void renderLogin_basic() throws Exception {
        mockMvc.perform(get("/account/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/login"))
                .andExpect(model().attribute("pageTitle", "Přihlášení"));
    }

    @Test
    @DisplayName("GET /account/register renders view and title")
    void renderRegister() throws Exception {
        mockMvc.perform(get("/account/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/register"))
                .andExpect(model().attribute("pageTitle", "Registrace"));
    }

    @Test
    @DisplayName("POST /account/register with validation errors stays on form")
    void register_validationErrors() throws Exception {
        mockMvc.perform(post("/account/register")
                        .param("email", "")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/register"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /account/register success redirects to /account/login and flashes success")
    void register_success() throws Exception {
        mockMvc.perform(post("/account/register")
                        .param("email", "user@example.com")
                        .param("password", "StrongPass123")
                        .param("confirmPassword", "StrongPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/login"))
                .andExpect(flash().attribute("success", "Uživatel zaregistrován."));

        var captor = ArgumentCaptor.forClass(RegisterDTO.class);
        verify(accountService).create(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("POST /account/register duplicate email returns form with field error")
    void register_duplicateEmail() throws Exception {
        doThrow(new DuplicateEmailException()).when(accountService).create(any(RegisterDTO.class));

        mockMvc.perform(post("/account/register")
                        .param("email", "dupe@example.com")
                        .param("password", "Abcdef1234!")
                        .param("confirmPassword", "Abcdef1234!"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/register"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("GET /account/change-password renders view and title")
    void renderChangePassword() throws Exception {
        mockMvc.perform(get("/account/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/change-password"))
                .andExpect(model().attribute("pageTitle", "Změna hesla"));
    }

    @Test
    @DisplayName("POST /account/change-password with validation errors stays on form")
    void changePassword_validationErrors() throws Exception {
        asUser(42L);
        mockMvc.perform(post("/account/change-password")
                        .param("currentPassword", "")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/change-password"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /account/change-password success redirects to insured detail with flash")
    void changePassword_success() throws Exception {
        long uid = 123L;
        asUser(uid);

        mockMvc.perform(post("/account/change-password")
                        .param("currentPassword", "OldPassword123")
                        .param("newPassword", "NewPassword123")
                        .param("confirmPassword", "NewPassword123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "Heslo změněno."))
                .andExpect(redirectedUrlPattern("/insured/*/detail"));

        var dtoCap = ArgumentCaptor.forClass(ChangePasswordDTO.class);
        var idCap = ArgumentCaptor.forClass(Long.class);
        verify(accountService).changePassword(idCap.capture(), dtoCap.capture());
        assertThat(idCap.getValue()).isEqualTo(uid);
        assertThat(dtoCap.getValue().getCurrentPassword()).isEqualTo("OldPassword123");
        assertThat(dtoCap.getValue().getNewPassword()).isEqualTo("NewPassword123");
        assertThat(dtoCap.getValue().getConfirmPassword()).isEqualTo("NewPassword123");
    }

    @Test
    @DisplayName("POST /account/change-password invalid current password returns form with field error")
    void changePassword_invalidCurrent() throws Exception {
        long uid = 55L;
        asUser(uid);

        doThrow(new InvalidPasswordException())
                .when(accountService).changePassword(any(Long.class), any(ChangePasswordDTO.class));

        mockMvc.perform(post("/account/change-password")
                        .param("currentPassword", "NotTheRightOne")   // >= 10 chars per DTO
                        .param("newPassword", "GoodPassword1")
                        .param("confirmPassword", "GoodPassword1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/account/change-password"))
                .andExpect(model().hasErrors());
    }
}
