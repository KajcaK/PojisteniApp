package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.models.enums.Role;

import java.util.Set;

public record CurrentUserDTO(Long userId, String email, Set<Role> roles) {}

