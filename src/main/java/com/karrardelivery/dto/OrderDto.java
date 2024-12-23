package com.karrardelivery.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
public class OrderDto {
    private String invoiceNo;
    private String deliveryAgent;
    private Date orderDate;
    private Date deliveryDate;
    private String address;
    private String longitude;
    private String latitude;
    private Long emirateId;
    private Long traderId;
    private String status;
    private Double totalAmount;
    private Double traderAmount;
    private Double deliveryAmount;
    private Double agentAmount;
    private Double netCompanyAmount;
    private String customerPhoneNo;
    private String comment;
}
