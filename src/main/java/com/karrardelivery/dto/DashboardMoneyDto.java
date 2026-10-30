package com.karrardelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Money for one month. Only DELIVERED orders count (by delivery date).
 * revenue = sum of delivery fees, netProfit = sum of company net (delivery fee - agent fee).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMoneyDto {
    /** yyyy-MM */
    private String month;
    private long deliveredCount;
    private double revenue;
    private double netProfit;
    /** the month before, for the trend (null for the previous month itself) */
    private DashboardMoneyDto previous;
}
