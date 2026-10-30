package com.karrardelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentTodayDto {
    private Long agentId;
    private String agentName;
    private String phoneNumber;
    /** orders with today's order date */
    private long assignedToday;
    /** orders delivered today */
    private long deliveredToday;
    /** orders currently under delivery */
    private long underDelivery;
    /** total amount of orders still under delivery + orders delivered today */
    private double cashToCollect;
}
