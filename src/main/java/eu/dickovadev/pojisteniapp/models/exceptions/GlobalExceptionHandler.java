package eu.dickovadev.pojisteniapp.models.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundException(
            NoHandlerFoundException ex,
            Model model
    ) {
        model.addAttribute("error", "Stránka nenalezena. Zkuste to znovu.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(
            Exception ex,
            Model model
    ) {
        model.addAttribute("error", "Došlo k chybě: " +  ex.getMessage());
        return "error";
    }

}
