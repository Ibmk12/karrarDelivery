package com.karrardelivery.service.impl;

import com.karrardelivery.common.utility.BeanUtilsHelper;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.mapper.OrderReportMapper;
import com.karrardelivery.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReportDataService {

    private final OrderRepository orderRepository;
    private final OrderReportMapper orderReportMapper;
    private final HttpServletRequest request;

    public List<OrderReportDto> fetchReportData(OrderSpec spec) {
        // Extract and validate delivery date range
        LocalDateTime[] deliveryRange = BeanUtilsHelper.getDeliveryDateRange(request);
        LocalDateTime startOfDay = deliveryRange[0];
        LocalDateTime endOfDay = deliveryRange[1];

        // Extract trader name
        String traderName = request.getParameter("traderName");
        if (traderName == null || traderName.isBlank()) {
            throw new IllegalArgumentException("Trader name is required");
        }

        // Fetch orders using custom native query
        List<Order> orders = orderRepository.findCustomOrders(traderName.trim(), startOfDay, endOfDay);

        // Map entities to DTOs
        return orderReportMapper.toDtoList(orders);
    }
}
