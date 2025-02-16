package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.entities.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.AccountDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.PasswordsDoNotEqualException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void create(AccountDTO user){
        if(!user.getPassword().equals(user.getConfirmPassword()))
            throw new PasswordsDoNotEqualException();

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.addRole(Role.ROLE_USER);

        try {
            userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username, " + username + " not found"));
    }
}
