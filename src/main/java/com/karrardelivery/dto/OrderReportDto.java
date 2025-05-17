package com.karrardelivery.dto;

import com.karrardelivery.entity.enums.EEmirate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
public class OrderReportDto {

    private int sequenceNo;
    private Date orderDate;
    private String invoiceNo;
    private EEmirate emirate;
    private double totalAmount;
    private double deliveryAmount;
    private double traderAmount;
    private Long no;
    private String status;

}
