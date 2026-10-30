package com.karrardelivery.service.impl;

import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.OrderHistoryDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.OrderHistory;
import com.karrardelivery.entity.enums.EOrderHistoryAction;
import com.karrardelivery.entity.management.User;
import com.karrardelivery.repository.OrderHistoryRepository;
import com.karrardelivery.repository.UserRepository;
import com.karrardelivery.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.karrardelivery.constant.Messages.DATA_FETCHED_SUCCESSFULLY;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private static final int MAX_VALUE_LENGTH = 500;

    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;

    // ---------------------------------------------------------------- write

    @Override
    public Map<String, String> snapshot(Order order) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("deliveryStatus", order.getDeliveryStatus() != null ? order.getDeliveryStatus().getEnglish() : null);
        values.put("agent", order.getAgent() != null ? order.getAgent().getName() : null);
        values.put("trader", order.getTrader() != null ? order.getTrader().getName() : null);
        values.put("traderAmount", amount(order.getTraderAmount()));
        values.put("deliveryAmount", amount(order.getDeliveryAmount()));
        values.put("agentAmount", amount(order.getAgentAmount()));
        values.put("totalAmount", amount(order.getTotalAmount()));
        values.put("invoiceNo", order.getInvoiceNo());
        values.put("customerPhoneNo", order.getCustomerPhoneNo());
        values.put("emirate", order.getEmirate() != null ? order.getEmirate().name() : null);
        values.put("address", order.getAddress());
        values.put("orderDate", day(order.getOrderDate()));
        values.put("deliveryDate", day(order.getDeliveryDate()));
        values.put("comment", order.getComment());
        return values;
    }

    @Override
    public void recordChanges(Order order, Map<String, String> before) {
        try {
            Map<String, String> after = snapshot(order);
            List<OrderHistory> rows = new ArrayList<>();
            Date now = new Date();
            String user = currentUser();
            boolean statusChanged = !Objects.equals(before.get("deliveryStatus"), after.get("deliveryStatus"));
            for (Map.Entry<String, String> entry : after.entrySet()) {
                String field = entry.getKey();
                // total is derived from the other amounts, don't log it twice
                if ("totalAmount".equals(field)) continue;
                // the delivery date is set automatically when the status changes
                if (statusChanged && "deliveryDate".equals(field)) continue;
                String oldValue = before.get(field);
                String newValue = entry.getValue();
                if (Objects.equals(oldValue, newValue)) continue;
                rows.add(build(order, actionFor(field), field, oldValue, newValue, user, now));
            }
            if (!rows.isEmpty()) orderHistoryRepository.saveAll(rows);
        } catch (Exception e) {
            // history must never break the actual update
            log.warn("Could not record order history for order {}: {}", order.getId(), e.getMessage());
        }
    }

    @Override
    public void record(Order order, EOrderHistoryAction action, String fieldName, String oldValue, String newValue) {
        try {
            orderHistoryRepository.save(build(order, action, fieldName, oldValue, newValue, currentUser(), new Date()));
        } catch (Exception e) {
            log.warn("Could not record order history for order {}: {}", order.getId(), e.getMessage());
        }
    }

    @Override
    public void recordAll(List<Order> orders, EOrderHistoryAction action) {
        try {
            String user = currentUser();
            Date now = new Date();
            List<OrderHistory> rows = orders.stream()
                    .filter(o -> o.getId() != null)
                    .map(o -> build(o, action, null, null, null, user, now))
                    .toList();
            orderHistoryRepository.saveAll(rows);
        } catch (Exception e) {
            log.warn("Could not record order history for {} orders: {}", orders.size(), e.getMessage());
        }
    }

    // ---------------------------------------------------------------- read

    @Override
    public GenericResponse<List<OrderHistoryDto>> getOrderHistory(Long orderId) {
        List<OrderHistory> rows = orderHistoryRepository.findByOrderIdOrderByChangedAtDescIdDesc(orderId);

        // show the user's name instead of the phone number when we can
        Map<String, String> names = new HashMap<>();
        rows.stream().map(OrderHistory::getChangedBy).filter(Objects::nonNull).distinct().forEach(phone ->
                userRepository.findByPhoneAndDeletedFalse(phone)
                        .map(this::fullName)
                        .ifPresent(name -> names.put(phone, name)));

        List<OrderHistoryDto> result = rows.stream().map(h -> OrderHistoryDto.builder()
                .id(h.getId())
                .orderId(h.getOrderId())
                .invoiceNo(h.getInvoiceNo())
                .action(h.getAction().name())
                .fieldName(h.getFieldName())
                .oldValue(h.getOldValue())
                .newValue(h.getNewValue())
                .changedBy(names.getOrDefault(h.getChangedBy(), h.getChangedBy()))
                .changedByPhone(h.getChangedBy())
                .changedAt(h.getChangedAt())
                .build()).toList();

        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    // ---------------------------------------------------------------- helpers

    private OrderHistory build(Order order, EOrderHistoryAction action, String field,
                               String oldValue, String newValue, String user, Date when) {
        OrderHistory h = new OrderHistory();
        h.setOrderId(order.getId());
        h.setInvoiceNo(order.getInvoiceNo());
        h.setAction(action);
        h.setFieldName(field);
        h.setOldValue(trim(oldValue));
        h.setNewValue(trim(newValue));
        h.setChangedBy(user);
        h.setChangedAt(when);
        return h;
    }

    private EOrderHistoryAction actionFor(String field) {
        return switch (field) {
            case "deliveryStatus" -> EOrderHistoryAction.STATUS_CHANGED;
            case "agent" -> EOrderHistoryAction.AGENT_ASSIGNED;
            case "traderAmount", "deliveryAmount", "agentAmount" -> EOrderHistoryAction.AMOUNT_CHANGED;
            default -> EOrderHistoryAction.UPDATED;
        };
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private String fullName(User user) {
        String name = Stream.of(user.getFirstName(), user.getMiddleName(), user.getLastName())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
        return name.isBlank() ? user.getPhone() : name;
    }

    private static String amount(Double value) {
        if (value == null) return null;
        return value % 1 == 0 ? String.valueOf(value.longValue()) : String.format(Locale.ROOT, "%.2f", value);
    }

    private static String day(Date date) {
        return date == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private static String trim(String value) {
        if (value == null) return null;
        return value.length() > MAX_VALUE_LENGTH ? value.substring(0, MAX_VALUE_LENGTH) : value;
    }
}
