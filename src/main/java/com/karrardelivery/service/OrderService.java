package com.karrardelivery.service;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.model.Order;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {

    Order updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    Order createOrder(OrderDto orderDto);
    List<Order> getAllOrders();
    List<OrderReportDto> getTraderReport(Long traderId, Date startDate, Date endDate);
}
