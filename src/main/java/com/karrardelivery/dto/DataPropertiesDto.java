package com.karrardelivery.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DataPropertiesDto {

    private Long totalElements;
    private Long totalPages;
    private Long pageNumber;
    private Long totalElementsPerPage;
    private Boolean isEmpty;
    private String sortedBy;
}
