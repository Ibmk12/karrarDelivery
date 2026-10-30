package com.karrardelivery.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.SavedOrderViewDto;
import com.karrardelivery.entity.SavedOrderView;
import com.karrardelivery.exception.ResourceNotFoundException;
import com.karrardelivery.repository.SavedOrderViewRepository;
import com.karrardelivery.service.SavedOrderViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.karrardelivery.constant.ErrorCodes.VALIDATION_ERR_CODE;
import static com.karrardelivery.constant.Messages.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedOrderViewServiceImpl implements SavedOrderViewService {

    private static final int MAX_VIEWS_PER_USER = 8;
    private static final int MAX_NAME_LENGTH = 60;
    private static final Set<String> ALLOWED_FILTERS = Set.of(
            "invoiceNo", "customerPhoneNo", "fromOrderDate", "toOrderDate",
            "totalAmount", "deliveryStatus", "traderName");

    private final SavedOrderViewRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public GenericResponse<List<SavedOrderViewDto>> getMyViews() {
        List<SavedOrderViewDto> result = repository.findByUserPhoneOrderByCreatedAtAscIdAsc(currentUser())
                .stream().map(this::toDto).toList();
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<SavedOrderViewDto> createView(SavedOrderViewDto dto) {
        String user = currentUser();
        String name = dto.getName() == null ? "" : dto.getName().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("View name is required");
        }
        if (name.length() > MAX_NAME_LENGTH) name = name.substring(0, MAX_NAME_LENGTH);
        if (repository.countByUserPhone(user) >= MAX_VIEWS_PER_USER) {
            throw new IllegalArgumentException("You can save up to " + MAX_VIEWS_PER_USER + " views");
        }

        // keep only known, non-empty filters
        Map<String, String> filters = new LinkedHashMap<>();
        if (dto.getFilters() != null) {
            dto.getFilters().forEach((k, v) -> {
                if (ALLOWED_FILTERS.contains(k) && v != null && !v.isBlank()) filters.put(k, v.trim());
            });
        }

        SavedOrderView view = new SavedOrderView();
        view.setUserPhone(user);
        view.setName(name);
        view.setFilters(writeJson(filters));
        view.setCreatedAt(new Date());
        repository.save(view);
        return GenericResponse.successResponse(toDto(view), "Saved view created successfully");
    }

    @Override
    public void deleteView(Long id) {
        // users can only delete their own views
        SavedOrderView view = repository.findByIdAndUserPhone(id, currentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Saved view not found", VALIDATION_ERR_CODE));
        repository.delete(view);
    }

    private SavedOrderViewDto toDto(SavedOrderView v) {
        return SavedOrderViewDto.builder()
                .id(v.getId())
                .name(v.getName())
                .filters(readJson(v.getFilters()))
                .build();
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No logged-in user");
        }
        return auth.getName();
    }

    private String writeJson(Map<String, String> filters) {
        try {
            return objectMapper.writeValueAsString(filters);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid filters", e);
        }
    }

    private Map<String, String> readJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.warn("Could not read saved view filters: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
