package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.service.impl.OrderExcelExportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(ApiUrls.EXPORT_ORDERS)
@Slf4j
@RequiredArgsConstructor
public class ExportController {

    private final OrderExcelExportService orderExcelExportService;

    @GetMapping(DAILY_REPORT)
    public void getOrdersDailyReport(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) {
        try {
            orderExcelExportService.getOrdersDailyReport(spec, response, request);
        } catch (Exception e) {
            log.error("Error exporting order report", e);
        }
    }

    @GetMapping(ORDER_REPORT)
    public void getOrdersReport(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) {
        try {
            orderExcelExportService.generateOrderReport(spec, response);
        } catch (Exception e) {
            log.error("Error exporting order report", e);
        }
    }

}