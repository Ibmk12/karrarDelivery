package com.karrardelivery.service;

import com.karrardelivery.dto.*;
import com.karrardelivery.entity.Order;
import com.karrardelivery.controller.spec.OrderSpec;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    GenericResponse<String> updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    GenericResponse<String> createOrder(OrderDto orderDto);
    GenericResponse<List<OrderDto>> getAllOrders(OrderSpec spec);
    GenericResponse<OrderDto> getOrderById(Long id);
    GenericResponse<String> updateOrdersStatus(UpdatedOrderStatusRequest request);
    List<OrderReportDto> getTraderReport(ReportDto reportDto);
    ResponseEntity<Void> exportExcelTemplate(HttpServletResponse response) throws IOException;
    void saveOrdersFromFile(MultipartFile file) throws IOException;
}
