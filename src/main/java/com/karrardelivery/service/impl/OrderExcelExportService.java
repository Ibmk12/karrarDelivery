package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderReportDto;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.karrardelivery.service.impl.ExcelExportService.Section;

@Service
@RequiredArgsConstructor
public class OrderExcelExportService {

    private final OrderRepository orderRepository;
    private final OrderReportMapper orderReportMapper;
    private final ExcelExportService excelExportService;
    private final MessageService messageService;

    public void exportOrdersToExcel(OrderSpec spec, HttpServletResponse response, HttpServletRequest request) throws IOException {

        Specification<Order> specification = Specification.where(spec).and(OrderSpec.applyDefaultStatusesIfMissing(spec));
        List<Order> orders = orderRepository.findAll(specification);
        List<OrderReportDto> dtos = orderReportMapper.toDtoList(orders);

        // Prepare headers
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
                .map(key -> messageService.getMessage(key))
                .toArray(String[]::new);

        List<Section<OrderReportDto>> sections = dtos.stream()
                .collect(Collectors.groupingBy(OrderReportDto::getDeliveryStatus))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<OrderReportDto> sectionData = entry.getValue();

                    // Create a rowMapper with index starting from 0
                    Function<OrderReportDto, Object[]> sectionRowMapper = dto -> {
                        int index = sectionData.indexOf(dto) + 1; // zero-based index
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

                    return new Section<>(
                            entry.getKey().toLowerCase(),
                            sectionData,
                            sectionRowMapper,
                            totalMapper
                    );
                })
                .toList();


        byte[] fileBytes = excelExportService.exportSectionsToExcel("Orders", headers, sections);

        String fileName = generateFileNameWithDateTime("orders-report", "xlsx");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getOutputStream().write(fileBytes);
        response.getOutputStream().flush();
    }

    private String generateFileNameWithDateTime(String baseName, String extension) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return baseName + "-" + now.format(formatter) + "." + extension;
    }
}
