package com.karrardelivery.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
public class OrderReportDto {

    private int sequenceNo;        // Sequential number for each record
    private Date orderDate;
    private String invoiceNo;
    private String emirate;
    private double totalAmount;
    private double deliveryAmount;
    private double traderAmount;
    private Long no;             // Assuming this refers to some specific "NO" field in the order
    private String status;

}
