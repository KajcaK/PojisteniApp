package eu.dickovadev.pojisteniapp.controllers;


import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.AccountDTO;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
import eu.dickovadev.pojisteniapp.models.exceptions.PasswordsDoNotEqualException;
import eu.dickovadev.pojisteniapp.services.AccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private static final String REGISTER_VIEW = "pages/account/register";
    private static final String LOGIN_VIEW = "pages/account/login";
    private static final String CHANGE_PASSWORD_VIEW = "pages/account/change-password";

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("login")
    public String renderLogin(Model model, HttpSession session) {

        model.addAttribute("pageTitle", "Přihlášení");

        String errorMessage = (String) session.getAttribute("error");
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            session.removeAttribute("error"); // Clear after use
        }

        return LOGIN_VIEW;
    }

    @GetMapping("register")
    public String renderRegister(
            @ModelAttribute AccountDTO accountDTO,
            Model model
    ) {
        model.addAttribute("pageTitle", "Registrace");
        return REGISTER_VIEW;
    }

    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute AccountDTO accountDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors())
            renderRegister(accountDTO, model);

        try {
            accountService.create(accountDTO);
        } catch (DuplicateEmailException ex) {
            result.rejectValue("email", "error", ex.getMessage());
            return REGISTER_VIEW;
        } catch (PasswordsDoNotEqualException ex) {
            result.rejectValue("password", "error", ex.getMessage());
            result.rejectValue("confirmPassword", "error", ex.getMessage());
            return REGISTER_VIEW;
        }

        redirectAttributes.addFlashAttribute("success", "Uživatel zaregistrován.");
        return "redirect:/account/login";
    }

    @GetMapping("change-password")
    public String renderChangePassword(
            @ModelAttribute ChangePasswordDTO changePasswordDTO,
            Model model
    ) {
        model.addAttribute("pageTitle", "Změna hesla");
        return CHANGE_PASSWORD_VIEW;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute ChangePasswordDTO changePasswordDTO,
            BindingResult result,
            @AuthenticationPrincipal UserEntity user,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            renderChangePassword(changePasswordDTO, model);
        }

        try {
            accountService.changePassword(
                    user.getUserId(),
                    changePasswordDTO.getCurrentPassword(),
                    changePasswordDTO.getNewPassword(),
                    changePasswordDTO.getConfirmPassword()
            );
            redirectAttributes.addFlashAttribute("success", "Heslo změněno.");
            return "redirect:/insured/" + user.getUserId() + "/detail";

        } catch (InvalidPasswordException ex) {
            result.rejectValue("currentPassword", "error", ex.getMessage());
        } catch (PasswordsDoNotEqualException ex) {
            result.rejectValue("newPassword", "error", ex.getMessage());
            result.rejectValue("confirmPassword", "error", ex.getMessage());
        }

        return CHANGE_PASSWORD_VIEW;
    }
}
