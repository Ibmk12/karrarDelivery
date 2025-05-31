package com.karrardelivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class OrderReportDtoList {

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
//    private LocalDateTime date;
    private String traderName;
    private List<OrderReportDto> orderList;
    private double grandTotalAmount;
    private double totalDeliveryAmount;
    private double totalTraderAmount;
}
