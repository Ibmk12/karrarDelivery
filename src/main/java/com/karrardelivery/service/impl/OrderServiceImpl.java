package com.karrardelivery.service.impl;

import com.karrardelivery.common.utility.Constants;
import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.model.Order;
import com.karrardelivery.repository.EmirateRepository;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
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
    public List<OrderReportDto> getTraderReport(Long traderId, Date startDate, Date endDate) {
        List<Order> orders = orderRepository.findByTraderIdAndOrderDateBetween(traderId, startDate, endDate);
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

//    private CellStyle createHeaderCellStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        style.setFont(font);
//        return style;
//    }
}
