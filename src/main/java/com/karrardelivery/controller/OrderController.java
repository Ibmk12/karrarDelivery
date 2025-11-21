package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.dto.*;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import com.karrardelivery.service.OrderService;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.service.impl.OrderImportService;
import com.karrardelivery.service.impl.OrderReportDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(ApiUrls.ORDERS)
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderReportDataService orderReportDataService;
    private final OrderImportService orderImportService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> createOrder(@RequestBody OrderDto orderDto) {
        GenericResponse<String> response = orderService.createOrder(orderDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<OrderDto>>> getAllOrders(
            OrderSpec spec,
            @PageableDefault(sort = {"orderDate", "id"}, direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> updateOrderMetadata(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        orderService.updateOrderMetadata(id, orderDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<GenericResponse<String>> updateOrderListStatus(@RequestBody UpdatedOrderStatusRequest request) {
        orderService.updateOrderListStatus(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<GenericResponse<String>> updateOrderStatus(@RequestBody OrderDto request) {
        orderService.updateOrderStatus(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(DAILY_REPORT)
    public ResponseEntity<GenericResponse<List<OrderReportDto>>> getDailyReport(
            OrderSpec spec,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderReportDataService.getDailyReport(spec, pageable));
    }

    @GetMapping(UNDER_DELIVERY)
    public ResponseEntity<GenericResponse<List<OrderDto>>> getOrdersUnderDeliveryLongerThan(
            OrderSpec orderSpec,
            @RequestParam(value = "days", required = false) Integer days,
            @PageableDefault(sort = "orderDate", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersUnderDeliveryLongerThan(days, pageable));
    }

    @PostMapping(UPLOAD_ORDERS)
    public ResponseEntity<?> uploadOrders(@RequestParam("file") MultipartFile file) {
        orderImportService.importOrdersFromExcel(file);
        return ResponseEntity.ok("Orders imported successfully.");
    }

    @GetMapping(COUNT_PER_MONTH)
    public ResponseEntity<GenericResponse<List<Map<String, Object>>>> getOrdersCountPerMonth(
            @RequestParam(defaultValue = "8") int months,
            @RequestParam(required = false) EDeliveryStatus status) {
        return ResponseEntity.ok(orderService.getOrdersCountPerMonth(months, status));
    }
}