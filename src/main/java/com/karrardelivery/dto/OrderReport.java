package com.karrardelivery.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class OrderReport {

    OrderReportDtoList deliveredOrders;
    OrderReportDtoList underdeliveryOrders;
    OrderReportDtoList cancelledOrders;

}
