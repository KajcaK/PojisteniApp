package eu.dickovadev.pojisteniapp.configuration;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (authentication != null && authentication.isAuthenticated()) {
            // Get the authenticated user
            UserEntity user = (UserEntity) authentication.getPrincipal();

            // Store the user ID in the session
            request.getSession().setAttribute("userId", user.getUserId());

            if (user.getRoles().contains(Role.ROLE_ADMIN)) {
                request.getSession().setAttribute("badge", "ADMIN");
            } else {
                request.getSession().setAttribute("badge", user.getFirstName() + " " + user.getLastName());
            }
        } else {
            response.sendRedirect("/account/login");
        }
        response.sendRedirect("/");
    }
}
