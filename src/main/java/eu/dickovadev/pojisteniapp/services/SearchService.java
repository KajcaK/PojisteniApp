package eu.dickovadev.pojisteniapp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    <T> Page<T> search(String query, String searchField, Class<T> dtoClass, Pageable pageable);
}
