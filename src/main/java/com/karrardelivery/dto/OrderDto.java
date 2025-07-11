package com.karrardelivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import com.karrardelivery.entity.enums.EEmirate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
public class OrderDto {
    private Long id;
    private String invoiceNo;
    private String deliveryAgent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date orderDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date deliveryDate;
    private String address;
    private String longitude;
    private String latitude;
    private String emirate;
    private Long traderId;
    private String deliveryStatus;
    private Double totalAmount;
    private Double traderAmount;
    private Double deliveryAmount;
    private Double agentAmount;
    private Double netCompanyAmount;
    private String customerPhoneNo;
    private String comment;
}
