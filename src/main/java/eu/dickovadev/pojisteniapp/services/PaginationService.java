package eu.dickovadev.pojisteniapp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaginationService {

    private <T> Map<String, Integer> calculatePaginationMetadata(Page<T> page) {
        Map<String, Integer> metadata = new HashMap<>();
        metadata.put("totalItems", (int) page.getTotalElements());
        metadata.put("totalPages", page.getTotalPages());
        metadata.put("currentPage", page.getNumber() + 1); // Spring is zero-indexed
        return metadata;
    }

    public <T> Map<String, Integer> getPaginationMetadata(Page<T> page) {
        return calculatePaginationMetadata(page);
    }

    public Pageable createPageable(int page, int pageSize) {
        return PageRequest.of(page - 1, pageSize); // Pageable is zero-indexed
    }
}

