package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.data.entities.UserEntity;
import eu.dickovadev.pojisteniapp.data.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.EmailAlreadyRegisteredException;
import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.models.mappers.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void create(
            @Valid @ModelAttribute UserProfileDTO userDTO
    ) {

        // Check if user already exists by email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEmailException();
        }

        // Create new user entity and map the DTO
        UserEntity newUser = new UserEntity();
        userMapper.updateUserEntity(userDTO, newUser);

        // Set default role
        newUser.setRole(Role.ROLE_POJISTENEC);

        // Save the new user
        userRepository.save(newUser);
    }

    @Override
    public List<UserProfileDTO> getAll(){
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(i -> userMapper.toDTO(i))
                .toList();
    }

    @Override
    public UserProfileDTO getById(long userId) {
        UserEntity fetchedUser = getUserByIdOrThrow(userId);

        return userMapper.toDTO(fetchedUser);
    }

    @Override
    public void edit(
            UserProfileDTO user
    ){
        UserEntity fetchedUser = getUserByIdOrThrow(user.getUserId());

        // Check if email has changed, and if it's already taken
        if (!fetchedUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new EmailAlreadyRegisteredException();
            }
        }

        // Update user entity and save
        userMapper.updateUserEntity(user, fetchedUser);
        userRepository.save(fetchedUser);
    }


    @Override
    public void remove(long userId){
        UserEntity fetchedEntity = getUserByIdOrThrow(userId);
        userRepository.delete(fetchedEntity);
    }

    private UserEntity getUserByIdOrThrow(long userId){
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private UserEntity getUserByEmailOrThrow(String email){
        return userRepository.findByEmail(email).orElseThrow(DuplicateEmailException::new);
    }
}
