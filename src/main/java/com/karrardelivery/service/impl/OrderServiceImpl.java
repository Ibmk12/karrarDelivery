package com.karrardelivery.service.impl;

import com.karrardelivery.common.utility.Constants;
import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.dto.ReportDto;
import com.karrardelivery.model.Emirate;
import com.karrardelivery.model.Order;
import com.karrardelivery.model.Trader;
import com.karrardelivery.repository.EmirateRepository;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    EmirateRepository emirateRepository;

    @Autowired
    TraderRepository traderRepository;

    @Override
    public Order createOrder(OrderDto orderDto) {
        Order order = new Order();
        // Populate order fields from DTO
        order.setInvoiceNo(orderDto.getInvoiceNo());
        order.setDeliveryAgent(orderDto.getDeliveryAgent());
        order.setLongitude(orderDto.getLongitude());
        order.setLatitude(orderDto.getLatitude());
        order.setAddress(orderDto.getAddress());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setTraderAmount(orderDto.getTraderAmount());
        order.setDeliveryAmount(orderDto.getDeliveryAmount());
        order.setAgentAmount(orderDto.getAgentAmount());
        order.setNetCompanyAmount(orderDto.getNetCompanyAmount());
        order.setCustomerPhoneNo(orderDto.getCustomerPhoneNo());
        order.setComment(orderDto.getComment());
        order.setStatus(Constants.ORDER_STATUS.UNDER_DELIVERY);
        order.setDeliveryDate(orderDto.getDeliveryDate());
        order.setOrderDate(orderDto.getOrderDate());
        order.setEmirate(emirateRepository.getReferenceById(orderDto.getEmirateId()));
        order.setTrader(traderRepository.getReferenceById(orderDto.getTraderId()));
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id).orElseThrow();
        Optional.ofNullable(orderDto.getInvoiceNo()).ifPresent(order::setInvoiceNo);
        Optional.ofNullable(orderDto.getDeliveryAgent()).ifPresent(order::setDeliveryAgent);
        Optional.ofNullable(orderDto.getTotalAmount()).ifPresent(order::setTotalAmount);
        Optional.ofNullable(orderDto.getTraderAmount()).ifPresent(order::setTraderAmount);
        Optional.ofNullable(orderDto.getDeliveryAmount()).ifPresent(order::setDeliveryAmount);
        Optional.ofNullable(orderDto.getAgentAmount()).ifPresent(order::setDeliveryAmount);
        Optional.ofNullable(orderDto.getNetCompanyAmount()).ifPresent(order::setNetCompanyAmount);
        Optional.ofNullable(orderDto.getCustomerPhoneNo()).ifPresent(order::setCustomerPhoneNo);
        Optional.ofNullable(orderDto.getComment()).ifPresent(order::setComment);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

//    public void exportTraderReport(Long traderId, LocalDate startDate, LocalDate endDate, ServletOutputStream outputStream) throws IOException {
//        List<Order> orders = orderRepository.findByTraderAndDateRange(traderId, startDate, endDate);
//
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Trader Report");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        String[] headers = {"No", "Date", "Invoice No", "Emirate", "Total Amount", "Delivery Amount", "Trader Amount", "NO", "Status"};
//        for (int i = 0; i < headers.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(headers[i]);
//            cell.setCellStyle(createHeaderCellStyle(workbook));
//        }
//
//        // Populate rows with data
//        int rowIndex = 1;
//        int no = 1;
//        for (Order order : orders) {
//            Row row = sheet.createRow(rowIndex++);
//            row.createCell(0).setCellValue(no++);
//            row.createCell(1).setCellValue(order.getOrderDate().toString());
//            row.createCell(2).setCellValue(order.getInvoiceNo());
//            row.createCell(3).setCellValue(order.getEmirate().getName());
//            row.createCell(4).setCellValue(order.getTotalAmount());
//            row.createCell(5).setCellValue(order.getDeliveryAmount());
//            row.createCell(6).setCellValue(order.getTraderAmount());
//            row.createCell(7).setCellValue(order.getNo());
//            row.createCell(8).setCellValue(order.getStatus());
//        }
//
//        workbook.write(outputStream);
//        workbook.close();
//    }

    // Method to return data as JSON
    @Override
    public List<OrderReportDto> getTraderReport(ReportDto reportDto) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {

            if(reportDto.getStart() == null || reportDto.getStart().isEmpty())
                sdf.parse(sdf.format(new Date()));
            else
                startDate = sdf.parse(reportDto.getStart());

            if(reportDto.getEnd() == null || reportDto.getEnd().isEmpty())
                sdf.parse(sdf.format(new Date()));
            else
                endDate = sdf.parse(reportDto.getEnd());

        }
        catch (Exception ex){
            log.error("Error parsing date");
        }

        List<Order> orders = orderRepository.findByTraderIdAndOrderDateBetween(reportDto.getTraderId(), startDate, endDate);
        List<OrderReportDto> reportDtos = new ArrayList<>();

        Long no = 1L;
        for (Order order : orders) {
            OrderReportDto dto = new OrderReportDto();
            dto.setNo(no++);
            dto.setOrderDate(order.getOrderDate());
            dto.setInvoiceNo(order.getInvoiceNo());
            dto.setEmirate(order.getEmirate().getName());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setDeliveryAmount(order.getDeliveryAmount());
            dto.setTraderAmount(order.getTraderAmount());
            dto.setNo(order.getId());
            dto.setStatus(order.getStatus());

            reportDtos.add(dto);
        }

        return reportDtos;
    }

    @Override
    public ResponseEntity<Void> exportExcelTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_template.xlsx");

        // Generate the Excel template
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Order Template");

        // Create header row with columns
        String[] headers = {"Date", "Invoice No", "Status", "Trader", "Emirate", "Delivery Agent", "Total Amount", "Trader Amount", "Delivery Amount", "Agent Amount", "Net Company Amount", "longitude", "latitude", "address", "Customer Phone No", "comment"};
        Row headerRow = sheet.createRow(0);

        // Style for the header row
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Auto-size columns for readability
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write workbook to response output stream
        workbook.write(response.getOutputStream());
        workbook.close();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public void saveOrdersFromFile(MultipartFile file) throws IOException {
        List<Order> orders = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // Create a mapping of column names to column indices
            Map<String, Integer> columnMapping = new HashMap<>();
            for (Cell cell : headerRow) {
                columnMapping.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 to skip headers
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Order order = new Order();

                // Retrieve each cell value by column name
                if (columnMapping.containsKey("Date")) {
                    Cell orderDateCell = row.getCell(columnMapping.get("Date"));
                    if (orderDateCell != null && orderDateCell.getCellType() == CellType.NUMERIC) {
                        order.setOrderDate(orderDateCell.getDateCellValue());
                    }
                }

                if (columnMapping.containsKey("Invoice No")) {
                    Cell invoiceNoCell = row.getCell(columnMapping.get("Invoice No"));
                    if (invoiceNoCell != null) {
                        order.setInvoiceNo(String.valueOf(invoiceNoCell.getNumericCellValue()));
                    }
                }

                if (columnMapping.containsKey("Trader")) {
                    Cell traderCell = row.getCell(columnMapping.get("Trader"));
                    if (traderCell != null) {
                        String traderName = traderCell.getStringCellValue();
                        Trader trader = traderRepository.findByName(traderName); // Assume findByName exists
                        order.setTrader(trader);
                    }
                }

                if (columnMapping.containsKey("Emirate")) {
                    Cell emirateCell = row.getCell(columnMapping.get("Emirate"));
                    if (emirateCell != null) {
                        String emirateName = emirateCell.getStringCellValue();
                        Emirate emirate = emirateRepository.findByName(emirateName); // Assume findByName exists
                        order.setEmirate(emirate);
                    }
                }

                if (columnMapping.containsKey("Status")) {
                    Cell statusCell = row.getCell(columnMapping.get("Status"));
                        String orderStatus = statusCell == null? Constants.ORDER_STATUS.UNDER_DELIVERY: String.valueOf(statusCell.getStringCellValue());
                        order.setStatus(Constants.ORDER_STATUS.getOrderStatus(orderStatus));
                }

                if (columnMapping.containsKey("Delivery Agent")) {
                    Cell deliveryAgent = row.getCell(columnMapping.get("Delivery Agent"));
                    if (deliveryAgent != null) {
                        order.setDeliveryAgent(deliveryAgent.getStringCellValue());
                    }
                }

                if (columnMapping.containsKey("Total Amount")) {
                    Cell totalAmountCell = row.getCell(columnMapping.get("Total Amount"));
                    if (totalAmountCell != null && totalAmountCell.getCellType() == CellType.NUMERIC) {
                        order.setTotalAmount(totalAmountCell.getNumericCellValue());
                    }
                }

                if (columnMapping.containsKey("Trader Amount")) {
                    Cell traderCellAmount = row.getCell(columnMapping.get("Trader Amount"));
                    if (traderCellAmount != null && traderCellAmount.getCellType() == CellType.NUMERIC) {
                        order.setTraderAmount(traderCellAmount.getNumericCellValue());
                    }
                }

                if (columnMapping.containsKey("Delivery Amount")) {
                    Cell deliveryAmountCell = row.getCell(columnMapping.get("Delivery Amount"));
                    if (deliveryAmountCell != null && deliveryAmountCell.getCellType() == CellType.NUMERIC) {
                        order.setDeliveryAmount(deliveryAmountCell.getNumericCellValue());
                    }
                }

                if (columnMapping.containsKey("Agent Amount")) {
                    Cell agentAmountCell = row.getCell(columnMapping.get("Agent Amount"));
                    if (agentAmountCell != null && agentAmountCell.getCellType() == CellType.NUMERIC) {
                        order.setAgentAmount(agentAmountCell.getNumericCellValue());
                    }
                }

                if (columnMapping.containsKey("Net Company Amount")) {
                    Cell netCompanyAmountCell = row.getCell(columnMapping.get("Net Company Amount"));
                    if (netCompanyAmountCell != null && netCompanyAmountCell.getCellType() == CellType.NUMERIC) {
                        order.setNetCompanyAmount(netCompanyAmountCell.getNumericCellValue());
                    }
                }

                if (columnMapping.containsKey("longitude")) {
                    Cell longitudeCell = row.getCell(columnMapping.get("longitude"));
                    if (longitudeCell != null) {
                        order.setLongitude(longitudeCell.getStringCellValue());
                    }
                }

                if (columnMapping.containsKey("latitude")) {
                    Cell latitudeCell = row.getCell(columnMapping.get("latitude"));
                    if (latitudeCell != null) {
                        order.setLatitude(latitudeCell.getStringCellValue());
                    }
                }

                if (columnMapping.containsKey("address")) {
                    Cell addressCell = row.getCell(columnMapping.get("address"));
                    if (addressCell != null) {
                        order.setAddress(addressCell.getStringCellValue());
                    }
                }

                if (columnMapping.containsKey("Customer Phone No")) {
                    Cell customerPhoneNoCell = row.getCell(columnMapping.get("Customer Phone No"));
                    if (customerPhoneNoCell != null) {
                        order.setCustomerPhoneNo(String.valueOf(customerPhoneNoCell.getNumericCellValue()));
                    }
                }

                orders.add(order);
            }

            // Save all orders in batch
            orderRepository.saveAll(orders);
        }
        catch (Exception ex){
            log.error("Somrthing went wrong:  ", ex);
            throw ex;
        }
    }
//    private CellStyle createHeaderCellStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        style.setFont(font);
//        return style;
//    }
}
