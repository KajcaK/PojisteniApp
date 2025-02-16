package eu.dickovadev.pojisteniapp.models.services;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaginationServiceImpl implements PaginationService {

    // Method to calculate pagination metadata
    private <T> Map<String, Integer> calculatePaginationMetadata(List<T> items, int page, int pageSize) {
        // Ensure valid page and pageSize values
        page = Math.max(1, page);  // Ensure page is at least 1
        pageSize = Math.max(1, pageSize);  // Ensure pageSize is at least 1

        int totalItems = items.size();
        int totalPages = (totalItems + pageSize - 1) / pageSize;

        // Create a map to hold pagination metadata
        Map<String, Integer> metadata = new HashMap<>();
        metadata.put("totalItems", totalItems);
        metadata.put("totalPages", totalPages);
        metadata.put("currentPage", page);

        return metadata;
    }

    @Override
    public <T> List<T> paginate(List<T> items, int page, int pageSize) {
        Map<String, Integer> metadata = calculatePaginationMetadata(items, page, pageSize);

        int startIndex = (metadata.get("currentPage") - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, metadata.get("totalItems"));

        // Handle empty list case or out-of-bounds pages
        if (startIndex >= items.size()) {
            return Collections.emptyList();
        }

        return items.subList(startIndex, endIndex);
    }

    @Override
    public <T> Map<String, Integer> getPaginationMetadata(List<T> items, int page, int pageSize) {
        // Directly return the pagination metadata map
        return calculatePaginationMetadata(items, page, pageSize);
    }
}
