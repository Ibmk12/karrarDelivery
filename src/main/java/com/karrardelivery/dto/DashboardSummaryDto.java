package com.karrardelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Numbers for the dashboard KPI cards. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    /** orders with an order date of today */
    private long createdToday;
    /** same, for the same weekday last week (for the trend) */
    private long createdSameDayLastWeek;
    /** orders delivered today */
    private long deliveredToday;
    private long deliveredSameDayLastWeek;
    /** orders currently under delivery */
    private long underDelivery;
    /** orders under delivery for more than {@link #delayedDays} days */
    private long delayed;
    private int delayedDays;
}
