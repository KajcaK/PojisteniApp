package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.dto.CurrentUserDTO;
import eu.dickovadev.pojisteniapp.models.dto.LoginDTO;
import eu.dickovadev.pojisteniapp.models.dto.RegisterDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.services.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountApiController {

    private static final Logger log = LoggerFactory.getLogger(AccountApiController.class);

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    public AccountApiController(AccountService accountService, AuthenticationManager authenticationManager) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("API registration attempt for email={}", registerDTO.getEmail());

        try {
            accountService.create(registerDTO);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Uživatel zaregistrován.",
                            "email", registerDTO.getEmail()
                    ));

        } catch (DuplicateEmailException ex) {
            log.warn("Duplicate email during API registration: {}", registerDTO.getEmail());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "field", "email",
                            "message", ex.getMessage()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("API login attempt for email={}", loginDTO.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity user = (UserEntity) authentication.getPrincipal();

            // later add JWT
            return ResponseEntity.ok(Map.of(
                    "message", "Přihlášení proběhlo úspěšně.",
                    "userId", user.getUserId(),
                    "email", user.getEmail()
            ));

        } catch (BadCredentialsException ex) {
            log.warn("Invalid login attempt for email={}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "type", "auth",
                            "message", "Neplatný e-mail nebo heslo."
                    ));
        } catch (Exception ex) {
            log.error("Unexpected error during API login for email={}", loginDTO.getEmail(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "type", "server",
                            "message", "Něco se pokazilo. Zkuste to prosím znovu."
                    ));
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("API change password requested");

        // must be authenticated
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity user)) {
            log.warn("Change password attempt without authenticated user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "type", "auth",
                            "message", "Pro změnu hesla se musíte přihlásit."
                    ));
        }

        // delegates all checks to service (including current password & access)
        accountService.changePassword(user.getUserId(), changePasswordDTO);

        return ResponseEntity.ok(Map.of(
                "message", "Heslo bylo úspěšně změněno."
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserEntity user)) {
            return ResponseEntity.status(401).build();
        }

        var dto = new CurrentUserDTO(user.getUserId(), user.getEmail(), user.getRoles());
        return ResponseEntity.ok(dto);
    }

}
