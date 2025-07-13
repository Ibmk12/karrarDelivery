package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.dto.*;
import com.karrardelivery.service.OrderService;
import com.karrardelivery.controller.spec.OrderSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(ApiUrls.ORDERS)
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

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
            @PageableDefault(size = 10) Pageable pageable) {
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

    @GetMapping(REPORT)
    public ResponseEntity<GenericResponse<OrderReportDtoList>> getOrderReport(OrderSpec spec) {
        return ResponseEntity.ok(orderService.getOrderReport(spec));
    }

    @GetMapping(REPORT_STATUS)
    public ResponseEntity<GenericResponse<OrderReport>> getOrderReportPerStatus(OrderSpec spec) {
        return ResponseEntity.ok(orderService.getOrderReportPerStatus(spec));
    }
}