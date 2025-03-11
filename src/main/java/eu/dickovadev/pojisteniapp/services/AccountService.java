package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.models.dto.AccountDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {

    void create(AccountDTO accountDTO);

    void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);
}
