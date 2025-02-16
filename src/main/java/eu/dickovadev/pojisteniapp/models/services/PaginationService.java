package eu.dickovadev.pojisteniapp.models.services;

import java.util.List;
import java.util.Map;

public interface PaginationService {
    <T> List<T> paginate(List<T> items, int page, int pageSize);
    <T> Map<String, Integer> getPaginationMetadata(List<T> items, int page, int pageSize);
}

