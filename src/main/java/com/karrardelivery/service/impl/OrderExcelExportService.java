package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public void getOrdersDailyReport(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) {
        try {
            List<OrderReportDto> dtos = orderReportDataService.fetchReportData(spec);
            String traderName = request.getParameter("traderName");
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Build sections
            List<Section<OrderReportDto>> sections = dtos.stream()
                    .collect(Collectors.groupingBy(OrderReportDto::getDeliveryStatus))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(status -> {
                        // Process groups in this order: delivered -> under delivery -> cancelled -> exchanged
                        switch (status.toLowerCase()) {
                            case "delivered":
                                return 1;
                            case "under delivery":
                                return 2;
                            case "canceled":
                                return 3;
                            case "exchanged":
                                return 4;
                            default:
                                return 999;
                        }
                    })))
                    .map(entry -> {
                        List<OrderReportDto> sectionData = entry.getValue();

                        Function<OrderReportDto, Object[]> sectionRowMapper = dto -> {
                            int index = sectionData.indexOf(dto) + 1;
                            return new Object[]{
                                    index,
                                    dto.getOrderDate() != null ? sdf.format(dto.getOrderDate()) : "",
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
            XSSFWorkbook workbook = excelExportService.createWorkbookWithSections(dtos.get(0).getTraderName(), headers, sections);
            Sheet sheet = workbook.getSheet(dtos.get(0).getTraderName());

            excelFormattingService.centerSheetLayout(sheet);
            excelFormattingService.insertLogo(workbook, sheet, LOGO_PATH);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] fileBytes = out.toByteArray();
            String fileName = generateFileNameWithDateTime(traderName.trim().replace(" ", "_"), "xlsx");

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // expose the header to JS
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.getOutputStream().write(fileBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            log.error("Failed to export orders to Excel: {}", e.getMessage(), e);
        }
    }

    private String generateFileNameWithDateTime(String baseName, String extension) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return baseName + "-" + now.format(formatter) + "." + extension;
    }

    public void generateOrderReport(OrderSpec orderSpec, HttpServletResponse response) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Orders");

            List<OrderDto> orders = orderReportDataService.fetchOrderListData(orderSpec);
            // Translate headers
            String[] headerKeys = {
                    "order.report.id", "order.report.invoiceNo", "order.report.deliveryAgent",
                    "order.report.orderDate", "order.report.deliveryDate", "order.report.address",
                    "order.report.emirate",
                    "order.report.traderName", "order.report.deliveryStatus", "order.report.totalAmount",
                    "order.report.traderAmount", "order.report.deliveryAmount", "order.report.agentAmount",
                    "order.report.netCompanyAmount", "order.report.customerPhoneNo", "order.report.comment"
            };

            String[] headers = Arrays.stream(headerKeys)
                    .map(messageService::getMessage)
                    .toArray(String[]::new);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Create rows for data
            int rowIdx = 1;
            for (OrderDto dto : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getId());
                row.createCell(1).setCellValue(dto.getInvoiceNo());
                row.createCell(2).setCellValue(dto.getAgentName() != null ? dto.getAgentName() : "");
                row.createCell(3).setCellValue(dto.getOrderDate() != null ? dto.getOrderDate().toString() : "");
                row.createCell(4).setCellValue(dto.getDeliveryDate() != null ? dto.getDeliveryDate().toString() : "");
                row.createCell(5).setCellValue(dto.getAddress());
                row.createCell(6).setCellValue(dto.getEmirate());
                row.createCell(7).setCellValue(dto.getTraderName() != null ? dto.getTraderName() : "");
                row.createCell(8).setCellValue(dto.getDeliveryStatus());
                row.createCell(9).setCellValue(dto.getTotalAmount() != null ? dto.getTotalAmount() : 0);
                row.createCell(10).setCellValue(dto.getTraderAmount() != null ? dto.getTraderAmount() : 0);
                row.createCell(11).setCellValue(dto.getDeliveryAmount() != null ? dto.getDeliveryAmount() : 0);
                row.createCell(12).setCellValue(dto.getAgentAmount() != null ? dto.getAgentAmount() : 0);
                row.createCell(13).setCellValue(dto.getNetCompanyAmount() != null ? dto.getNetCompanyAmount() : 0);
                row.createCell(14).setCellValue(dto.getCustomerPhoneNo());
                row.createCell(15).setCellValue(dto.getComment());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            excelFormattingService.addTableBordersAndBoldHeader(sheet);
            // Write to HTTP response
            String fileName = "orders-report-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Error generating order report: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating order report", e);
        }
    }

}