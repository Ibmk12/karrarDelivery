package com.karrardelivery.service;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.model.Order;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {

    Order updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    Order createOrder(OrderDto orderDto);
    List<Order> getAllOrders();
    List<OrderReportDto> getTraderReport(Long traderId, Date startDate, Date endDate);
    ResponseEntity<Void> exportExcelTemplate(HttpServletResponse response) throws IOException;
}
