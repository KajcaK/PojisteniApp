package eu.dickovadev.pojisteniapp.models.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundException(
            NoHandlerFoundException ex,
            Model model
    ) {
        model.addAttribute("error", "Stránka nenalezena. Zkuste to znovu.");
        return "404";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyRegisteredException(
            RedirectAttributes redirectAttributes,
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {

        String referer = request.getHeader("Referer");

        if (referer != null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + referer;  // Redirects back to the page the user came from
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }


    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(
            RedirectAttributes redirectAttributes,
            UserNotFoundException ex,
            HttpServletRequest request
    ) {

        String referer = request.getHeader("Referer");

        if (referer != null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + referer;  // Redirects to the page the user came from
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    public String handlePolicyNotFoundException(
            RedirectAttributes redirectAttributes,
            PolicyNotFoundException ex,
            HttpServletRequest request
    ) {

        String referer = request.getHeader("Referer");

        if (referer != null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + referer;  // Redirects to the page the user came from
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(InvalidPolicyDatesException.class)
    public String handleInvalidPolicyDatesException(
            RedirectAttributes redirectAttributes,
            InvalidPolicyDatesException ex,
            HttpServletRequest request
    ) {

        String referer = request.getHeader("Referer");

        if (referer != null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + referer; // Redirects to the page the user came from
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(AmountPaidExceedException.class)
    public String handleAmountPaidExceedException(
            RedirectAttributes redirectAttributes,
            AmountPaidExceedException ex,
            HttpServletRequest request
    ) {
        String referer = request.getHeader("Referer");

        if (referer != null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + referer; // Redirects to the page the user came from
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(EventNotFoundException.class)
    public String handleEventNotFoundException(
            RedirectAttributes redirectAttributes,
            EventNotFoundException ex,
            HttpServletRequest request
    ) {
        String referer = request.getHeader("Referer");

        if (referer != null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + referer; // Redirects to the page the user came from
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(
            AccessDeniedException ex,
            Model model
    ) {
        model.addAttribute("error", "Přístup zamítnut.");
        return "redirect:/access-denied";
    }

    // Handle all other general exceptions
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(
            Exception ex,
            Model model
    ) {
        model.addAttribute("error", "Vyskytla se neočekávaná chyba. Zkuste to znovu.");
        return "error";
    }
}
