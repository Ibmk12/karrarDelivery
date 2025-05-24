package com.karrardelivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class OrderReportDtoList {

    private String deliverStatus;
    private String traderName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;
    private double grandTotal;
    private double totalDeliveryAmount;
    private double totalTraderAmount;
    private List<OrderReportDto> orderList;
}
