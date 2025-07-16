package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import com.karrardelivery.mapper.OrderMapper;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderImportService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final TraderRepository traderRepository;
    private final MessageService messageService;

    public void importOrdersFromExcel(MultipartFile file) {
        List<Order> orders = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new IllegalArgumentException("Sheet is empty");
            }

            // Build header index map
            Map<String, Integer> headerIndexMap = new HashMap<>();
            Row headerRow = rowIterator.next();
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim().toLowerCase();
                headerIndexMap.put(header, cell.getColumnIndex());
            }

            // Required headers
            String[] requiredHeaders = {
                    "invoice no", "delivery agent", "order date", "delivery date", "address",
                    "emirate", "trader", "delivery status", "total amount",
                    "trader amount", "delivery amount", "agent amount", "net company amount",
                    "customer phone no", "comment"
            };

            for (String header : requiredHeaders) {
                if (!headerIndexMap.containsKey(header)) {
                    throw new IllegalArgumentException("Missing header: " + header);
                }
            }

            // Read rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                OrderDto dto = new OrderDto();

                dto.setInvoiceNo(getSafeString(row, headerIndexMap, "invoice no"));
                dto.setDeliveryAgent(getSafeString(row, headerIndexMap, "delivery agent"));
                dto.setOrderDate(getDate(row, headerIndexMap, "order date"));
                dto.setDeliveryDate(getDate(row, headerIndexMap, "delivery date"));
                dto.setAddress(getSafeString(row, headerIndexMap, "address"));
                dto.setLongitude(getSafeString(row, headerIndexMap, "longitude"));
                dto.setLatitude(getSafeString(row, headerIndexMap, "latitude"));
                dto.setEmirate(getSafeString(row, headerIndexMap, "emirate"));
                dto.setDeliveryStatus(getSafeString(row, headerIndexMap, "delivery status"));
                dto.setTotalAmount(getDouble(row, headerIndexMap, "total amount"));
                dto.setTraderAmount(getDouble(row, headerIndexMap, "trader amount"));
                dto.setDeliveryAmount(getDouble(row, headerIndexMap, "delivery amount"));
                dto.setAgentAmount(getDouble(row, headerIndexMap, "agent amount"));
                dto.setNetCompanyAmount(getDouble(row, headerIndexMap, "net company amount"));
                dto.setCustomerPhoneNo(getSafeString(row, headerIndexMap, "customer phone no"));
                dto.setComment(getSafeString(row, headerIndexMap, "comment"));

                // Map and resolve trader
                String traderName = getSafeString(row, headerIndexMap, "trader");
                Trader trader = Optional.ofNullable(traderName)
                        .map(traderRepository::findByName)
                        .orElse(null);

                Order order = orderMapper.toEntity(dto);
                order.setTrader(trader);

                orders.add(order);
            }

            orderRepository.saveAll(orders);
            log.info("Imported {} orders from Excel", orders.size());

        }
        catch (DataIntegrityViolationException ex) {
            String rootMessage = Optional.ofNullable(ex.getRootCause())
                    .map(Throwable::getMessage)
                    .orElse("");

            if (rootMessage.contains("uc_orders_invoice_no") || rootMessage.contains("Duplicate entry")) {
                String localizedMessage = messageService.getMessage("order.import.duplicate_invoice");
                throw new RuntimeException(localizedMessage);
            }

            log.error("Data integrity violation during import", ex);
            throw new RuntimeException("Data integrity violation: " + ex.getMessage(), ex);
        }
        catch (Exception e) {
            log.error("Error importing orders from Excel", e);
            throw new RuntimeException("Failed to import orders: " + e.getMessage(), e);
        }
    }

    private String getSafeString(Row row, Map<String, Integer> map, String key) {
        if (!map.containsKey(key)) return null;
        Cell cell = row.getCell(map.get(key));
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        return cell.toString().trim();
    }

    private Double getDouble(Row row, Map<String, Integer> map, String key) {
        try {
            String val = getSafeString(row, map, key);
            return (val == null || val.isEmpty()) ? null : Double.parseDouble(val);
        } catch (Exception e) {
            return null;
        }
    }

    private Date getDate(Row row, Map<String, Integer> map, String key) {
        try {
            if (!map.containsKey(key)) return null;
            Cell cell = row.getCell(map.get(key));
            return (cell != null && DateUtil.isCellDateFormatted(cell)) ? cell.getDateCellValue() : null;
        } catch (Exception e) {
            return null;
        }
    }
}