package com.karrardelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDto {
    private Long id;
    private Long orderId;
    private String invoiceNo;
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
    /** user's full name when known, otherwise the phone number */
    private String changedBy;
    private String changedByPhone;
    private Date changedAt;
}
