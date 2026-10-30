package com.karrardelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedOrderViewDto {
    private Long id;
    private String name;
    /** filter name -> value (invoiceNo, customerPhoneNo, fromOrderDate, toOrderDate, totalAmount, deliveryStatus, traderName) */
    private Map<String, String> filters;
}
