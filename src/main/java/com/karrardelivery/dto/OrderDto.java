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
    private long emirateId;
    private long traderId;
    private String status;
    private double totalAmount;
    private double traderAmount;
    private double deliveryAmount;
    private double agentAmount;
    private double netCompanyAmount;
    private String customerPhoneNo;
    private String comment;
}
