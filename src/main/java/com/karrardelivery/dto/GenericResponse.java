package com.karrardelivery.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {
    private String message;
    private T data;
    private DataPropertiesDto dataProperties;

    public static <T> GenericResponse<T> successResponse(T data, String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> successResponseWithoutData(String message) {
        return GenericResponse.<T>builder()
                .message(message)
                .build();
    }

    public static <T> GenericResponse<T> errorResponse(String message) {
        return GenericResponse.<T>builder()
                .message(message != null ? message : "ERROR!")
                .build();
    }

    public static <T> GenericResponse<T> successResponseWithPagination(
            T data,
            Page page,
            String message
    ) {
        DataPropertiesDto dataProperties = DataPropertiesDto.builder()
                .totalElements(page.getTotalElements())
                .totalPages((long) page.getTotalPages())
                .pageNumber((long) page.getNumber())
                .totalElementsPerPage((long) page.getNumberOfElements())
                .isEmpty(page.isEmpty())
                .sortedBy(page.getSort() == null ? Sort.unsorted().toString() : page.getSort().toString())
                .build();

        return GenericResponse.<T>builder()
                .message(message)
                .data(data)
                .dataProperties(dataProperties)
                .build();
    }


}

