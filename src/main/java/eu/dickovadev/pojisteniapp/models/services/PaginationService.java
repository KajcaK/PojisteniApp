package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;

import java.util.List;
import java.util.Map;

public interface PaginationService {

    List<UserProfileDTO> paginate(List<UserProfileDTO> items, int page, int pageSize);

    Map<String, Integer> getPaginationMetadata(List<UserProfileDTO> items, int page, int pageSize);
}
