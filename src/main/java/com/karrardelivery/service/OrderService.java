package com.karrardelivery.service;

import com.karrardelivery.dto.*;
import com.karrardelivery.entity.Order;
import com.karrardelivery.controller.spec.OrderSpec;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    GenericResponse<String> updateOrderMetadata(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    GenericResponse<String> createOrder(OrderDto orderDto);
    GenericResponse<List<OrderDto>> getAllOrders(OrderSpec spec, Pageable pageable);
    GenericResponse<OrderReportDtoList> getOrderReport(OrderSpec spec);
    GenericResponse<OrderReport> getOrderReportPerStatus(OrderSpec spec);
    GenericResponse<OrderDto> getOrderById(Long id);
    GenericResponse<String> updateOrderListStatus(UpdatedOrderStatusRequest request);
    GenericResponse<String> updateOrderStatus(OrderDto request);
}
