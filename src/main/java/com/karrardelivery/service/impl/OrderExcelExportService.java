package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.dto.OrderReportDtoList;
import com.karrardelivery.dto.OrderReport;
import com.karrardelivery.entity.Order;
import com.karrardelivery.mapper.OrderReportMapper;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderExcelExportService {

    private final OrderRepository orderRepository;
    private final OrderReportMapper orderReportMapper;
    private final ExcelExportService excelExportService;
    private final MessageService messageService;

    public void exportOrdersToExcel(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Specification<Order> specification = Specification.where(spec);
        List<Order> orders = orderRepository.findAll(specification);
        List<OrderReportDto> dtos = orderReportMapper.toDtoList(orders);
        Locale locale = request.getLocale();

        String[] headerKeys = {
                "report.header.no",
                "report.header.date",
                "report.header.invoiceNo",
                "report.header.emirate",
                "report.header.totalAmount",
                "report.header.deliveryAmount",
                "report.header.traderAmount",
                "report.header.customerPhone",
                "report.header.status"
        };

        String[] headers = Stream.of(headerKeys)
                .map(key -> messageService.getMessage(key, null, locale))
                .toArray(String[]::new);

        Function<OrderReportDto, Object[]> rowMapper = dto -> new Object[] {
                dto.getSequenceNo(),
                dto.getOrderDate(),
                dto.getInvoiceNo(),
                dto.getEmirate(),
                dto.getTotalAmount(),
                dto.getDeliveryAmount(),
                dto.getTraderAmount(),
                dto.getCustomerPhoneNo(),
                dto.getDeliveryStatus()
        };

        byte[] fileBytes = excelExportService.exportToExcel("Orders", headers, dtos, rowMapper);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders-report.xlsx");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getOutputStream().write(fileBytes);
        response.getOutputStream().flush();
    }
}
