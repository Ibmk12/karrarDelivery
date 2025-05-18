package com.karrardelivery.service;

import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.dto.ReportDto;
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
    List<OrderReportDto> getTraderReport(ReportDto reportDto);
    ResponseEntity<Void> exportExcelTemplate(HttpServletResponse response) throws IOException;
    void saveOrdersFromFile(MultipartFile file) throws IOException;
}
