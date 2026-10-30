package com.karrardelivery.service;

import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.OrderHistoryDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.enums.EOrderHistoryAction;

import java.util.List;
import java.util.Map;

public interface OrderHistoryService {

    /** Snapshot of the fields we track, taken before a change (see {@link #recordChanges}). */
    Map<String, String> snapshot(Order order);

    /** Records one row per field that differs between {@code before} and the order's current values. */
    void recordChanges(Order order, Map<String, String> before);

    void record(Order order, EOrderHistoryAction action, String fieldName, String oldValue, String newValue);

    void recordAll(List<Order> orders, EOrderHistoryAction action);

    GenericResponse<List<OrderHistoryDto>> getOrderHistory(Long orderId);
}
