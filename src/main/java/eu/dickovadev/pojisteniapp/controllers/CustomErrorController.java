package eu.dickovadev.pojisteniapp.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(
            Model model,
            HttpServletRequest request
    ) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Stránka nenalezena");
                model.addAttribute("pageTitle", "PojištěníApp - 404");
                return "404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "Přístup zamítnut.");
                model.addAttribute("pageTitle", "Přístup zamítnut.");
                return "access-denied";
            }
        }

        model.addAttribute("pageTitle", "PojištěníApp - Chyba");
        return "error";
    }

    @GetMapping("/access-denied")
    public String accessDeniedPage(Model model) {
        model.addAttribute("pageTitle", "PojištěníApp - Přístup zamítnut");
        return "access-denied";
    }
}
