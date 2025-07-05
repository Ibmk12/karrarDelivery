package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.karrardelivery.constant.ApiUrls.LOGO_PATH;
import static com.karrardelivery.service.impl.ExcelExportService.Section;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExcelExportService {

    private final OrderReportDataService orderReportDataService;
    private final ExcelExportService excelExportService;
    private final ExcelFormattingService excelFormattingService;
    private final MessageService messageService;

    public void exportOrdersToExcel(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) {
        try {
            List<OrderReportDto> dtos = orderReportDataService.fetchReportData(spec);

            // Header translation
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
                    .map(messageService::getMessage)
                    .toArray(String[]::new);

            // Build sections
            List<Section<OrderReportDto>> sections = dtos.stream()
                    .collect(Collectors.groupingBy(OrderReportDto::getDeliveryStatus))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        List<OrderReportDto> sectionData = entry.getValue();

                        Function<OrderReportDto, Object[]> sectionRowMapper = dto -> {
                            int index = sectionData.indexOf(dto) + 1;
                            return new Object[]{
                                    index,
                                    dto.getOrderDate(),
                                    dto.getInvoiceNo(),
                                    dto.getEmirate(),
                                    dto.getTotalAmount(),
                                    dto.getDeliveryAmount(),
                                    dto.getTraderAmount(),
                                    dto.getCustomerPhoneNo(),
                                    dto.getDeliveryStatus()
                            };
                        };

                        Function<List<OrderReportDto>, Object[]> totalMapper = list -> new Object[]{
                                messageService.getMessage("report.total"),
                                "", "", "",
                                list.stream().mapToDouble(OrderReportDto::getTotalAmount).sum(),
                                list.stream().mapToDouble(OrderReportDto::getDeliveryAmount).sum(),
                                list.stream().mapToDouble(OrderReportDto::getTraderAmount).sum(),
                                "", ""
                        };

                        Function<List<OrderReportDto>, Object[]> receivedByTraderMapper = list -> new Object[]{
                                messageService.getMessage("report.received.by.trader"),
                                "", "", "",
                                list.stream().mapToDouble(OrderReportDto::getTraderAmount).sum(),
                                "",
                                "",
                                "", ""
                        };
                        return new Section<>(
                                entry.getKey().toLowerCase(),
                                sectionData,
                                sectionRowMapper,
                                totalMapper,
                                receivedByTraderMapper
                        );
                    })
                    .toList();

            // Excel processing
            XSSFWorkbook workbook = excelExportService.createWorkbookWithSections("Orders", headers, sections);
            Sheet sheet = workbook.getSheet("Orders");

            excelFormattingService.centerSheetLayout(sheet);
            excelFormattingService.insertLogo(workbook, sheet, LOGO_PATH);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] fileBytes = out.toByteArray();
            String fileName = generateFileNameWithDateTime("orders-report", "xlsx");

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.getOutputStream().write(fileBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            log.error("Failed to export orders to Excel: {}", e.getMessage(), e);
        }
    }

    private String generateFileNameWithDateTime(String baseName, String extension) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return baseName + "-" + now.format(formatter) + "." + extension;
    }
}