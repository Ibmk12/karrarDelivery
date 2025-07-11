package com.karrardelivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import com.karrardelivery.entity.enums.EEmirate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
public class OrderReportDto {

    private long sequenceNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date orderDate;
    private String invoiceNo;
    private String emirate;
    private String traderName;
    private double totalAmount;
    private double deliveryAmount;
    private double traderAmount;
    private String customerPhoneNo;
    private String deliveryStatus;
    private String traderCode;
    private String reportDate;
}
