package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.dto.RegisterDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {

    void create(RegisterDTO registerDTO);

    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
}
