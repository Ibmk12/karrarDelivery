package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.dto.*;
import com.karrardelivery.service.OrderService;
import com.karrardelivery.service.impl.OrderExcelExportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.karrardelivery.constant.ApiUrls.REPORT;
import static com.karrardelivery.constant.ApiUrls.REPORT_STATUS;

@RestController
@RequestMapping(ApiUrls.EXPORT_ORDERS)
@Slf4j
@RequiredArgsConstructor
public class ExportController {

    private final OrderExcelExportService orderExcelExportService;

    @GetMapping
    public void getOrderReportPerStatusReport(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) {
        try {
            orderExcelExportService.exportOrdersToExcel(spec, response, request);
        } catch (Exception e) {
            log.error("Error exporting order report", e);
        }
    }
}