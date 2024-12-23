package com.karrardelivery.controller;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.dto.ReportDto;
import com.karrardelivery.model.Order;
import com.karrardelivery.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/export")
//    public ResponseEntity<Void> exportTraderReportToExcel(@RequestParam Long traderId,
//                                                          @RequestParam String startDate,
//                                                          @RequestParam String endDate,
//                                                          HttpServletResponse response) throws IOException {
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trader_report.xlsx");
//
//        LocalDate start = LocalDate.parse(startDate);
//        LocalDate end = LocalDate.parse(endDate);
//        orderService.exportTraderReport(traderId, start, end, response.getOutputStream());
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping("/report")
    public ResponseEntity<List<OrderReportDto>> getTraderReport(@RequestBody ReportDto reportDto) throws Exception{
        List<OrderReportDto> report = orderService.getTraderReport(reportDto);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/export-template")
    public ResponseEntity<Void> exportOrderTemplate(HttpServletResponse response) throws IOException {
        return orderService.exportExcelTemplate(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadOrders(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received uploadOrders request: {}", file);
            orderService.saveOrdersFromFile(file);
            return ResponseEntity.status(HttpStatus.OK).body("Orders uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload orders: " + e.getMessage());
        }
    }
}
