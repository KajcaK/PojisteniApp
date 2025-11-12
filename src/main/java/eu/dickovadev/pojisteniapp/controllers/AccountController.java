package eu.dickovadev.pojisteniapp.controllers;


import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.RegisterDTO;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
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

import static eu.dickovadev.pojisteniapp.controllers.InsuredController.REDIRECT_DETAIL;

@Controller
@RequestMapping("/account")
public class AccountController {

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
        if (result.hasErrors()) {
            return VIEW_REGISTER;
        }

        try {
            accountService.create(registerDTO);
        } catch (DuplicateEmailException ex) {
            result.rejectValue("email", "error", ex.getMessage());
            return VIEW_REGISTER;
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
            return CHANGE_PASSWORD_VIEW;
        }

        try {
            accountService.changePassword(
                    user.getUserId(),
                    changePasswordDTO
            );
            redirectAttributes.addFlashAttribute("success", "Heslo změněno.");
            return String.format(REDIRECT_DETAIL, user.getUserId());

        } catch (InvalidPasswordException ex) {
            result.rejectValue("currentPassword", "error", ex.getMessage());
            return CHANGE_PASSWORD_VIEW;
        }
    }

}
