package com.karrardelivery.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdatedOrderStatusRequest {

    private String deliveryStatus;
    private List<String> orderList;
}
