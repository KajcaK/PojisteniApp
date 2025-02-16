package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.entities.InsurancePolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.entities.repositories.InsurancePolicyRepository;
import eu.dickovadev.pojisteniapp.entities.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.EmailAlreadyRegisteredException;
import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.models.mappers.PolicyMapper;
import eu.dickovadev.pojisteniapp.models.mappers.UserMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    @Override
    public void create(
            @Valid @ModelAttribute UserDTO userDTO
    ) {

        // Check if user already exists by email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEmailException();
        }

        // Create new user entity and map the DTO
        UserEntity newUser = new UserEntity();
        userMapper.updateUserEntity(userDTO, newUser);

        // Set default role
        newUser.addRole(Role.ROLE_POJISTENEC);

        // Save the new user
        userRepository.save(newUser);

        // Update the DTO with the newly generated userId
        userDTO.setUserId(newUser.getUserId()); // needed for redirect
    }

    @Override
    public List<UserDTO> getAll(){
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(i -> userMapper.toDTO(i))
                .toList();
    }

    @Override
    public UserDTO getById(long userId) {
        // Fetch the UserEntity from the database
        UserEntity fetchedUser = getUserByIdOrThrow(userId);

        // Convert the UserEntity to UserDTO
        UserDTO userDTO = userMapper.toDTO(fetchedUser);

        // Fetch the policies where the user is either the policyHolder or insuredUser
        Set<InsurancePolicyEntity> allPolicies = insurancePolicyRepository
                .findByPolicyHolderOrInsuredUser(fetchedUser, fetchedUser);

        // Map the fetched policies to PolicyDTOs
        Set<PolicyDTO> policyDTOs = allPolicies.stream()
                .map(policy -> policyMapper.toDTO(policy))
                .collect(Collectors.toSet());

        // Set the mapped policies in the UserDTO
        userDTO.setPolicies(policyDTOs);

        // Return the populated UserDTO
        return userDTO;
    }

    @Override
    public void edit(
            UserDTO user
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
    @Transactional
    public void remove(long userId){
        UserEntity fetchedEntity = getUserByIdOrThrow(userId);
        // delete policies where user is policyHolder or insuredUser
        insurancePolicyRepository.deleteByPolicyHolderOrInsuredUser(fetchedEntity, fetchedEntity);
        userRepository.delete(fetchedEntity);
    }

    @Override
    public UserEntity getEntityById(long userId) {
        // Retrieve the UserEntity based on userId
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private UserEntity getUserByIdOrThrow(long userId){
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
