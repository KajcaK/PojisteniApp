package eu.dickovadev.pojisteniapp.controllers;


import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.RegisterDTO;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
import eu.dickovadev.pojisteniapp.services.AccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static eu.dickovadev.pojisteniapp.controllers.InsuredController.REDIRECT_DETAIL;

@Controller
@RequestMapping("/account")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    private static final String VIEW_REGISTER = "pages/account/register";
    private static final String VIEW_LOGIN = "pages/account/login";
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

        return VIEW_LOGIN;
    }

    @GetMapping("register")
    public String renderRegister(
            @ModelAttribute RegisterDTO registerDTO,
            Model model
    ) {
        model.addAttribute("pageTitle", "Registrace");
        return VIEW_REGISTER;
    }

    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute RegisterDTO registerDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        log.info("Registration attempt for email={}", registerDTO.getEmail());

        if (result.hasErrors()) {
            log.debug("Validation failed for registration form: {}", result.getAllErrors());
            return VIEW_REGISTER;
        }

        try {
            accountService.create(registerDTO);
            log.info("User successfully registered: {}", registerDTO.getEmail());
            redirectAttributes.addFlashAttribute("success", "Uživatel zaregistrován.");
            return "redirect:/account/login";

        } catch (DuplicateEmailException ex) {
            log.warn("Duplicate email during registration: {}", registerDTO.getEmail());
            result.rejectValue("email", "error", ex.getMessage());
            return VIEW_REGISTER;

        } catch (Exception ex) {
            log.error("Unexpected error during registration for email={}", registerDTO.getEmail(), ex);
            throw ex;
        }
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
        log.info("Password change request for userId={}", user.getUserId());

        if (result.hasErrors()) {
            log.debug("Validation errors on password change form for userId={}", user.getUserId());
            return CHANGE_PASSWORD_VIEW;
        }

        try {
            accountService.changePassword(user.getUserId(), changePasswordDTO);
            log.info("Password successfully changed for userId={}", user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Heslo změněno.");
            return String.format(REDIRECT_DETAIL, user.getUserId());
        } catch (InvalidPasswordException ex) {
            log.warn("Invalid current password entered for userId={}", user.getUserId());
            result.rejectValue("currentPassword", "error", ex.getMessage());
            return CHANGE_PASSWORD_VIEW;
        } catch (Exception ex) {
            log.error("Unexpected error during password change for userId={}", user.getUserId(), ex);
            throw ex;
        }
    }
}
