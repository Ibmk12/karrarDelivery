package com.karrardelivery.service;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.model.Order;

import java.util.List;

public interface OrderService {

    Order updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    Order createOrder(OrderDto orderDto);
    List<Order> getAllOrders();
}
