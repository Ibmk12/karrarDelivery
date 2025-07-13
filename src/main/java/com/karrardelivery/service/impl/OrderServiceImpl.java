package com.karrardelivery.service.impl;

import com.karrardelivery.common.utility.BeanUtilsHelper;
import com.karrardelivery.constant.Messages;
import com.karrardelivery.dto.*;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import com.karrardelivery.exception.DuplicateResourceException;
import com.karrardelivery.exception.ResourceNotFoundException;
import com.karrardelivery.mapper.OrderMapper;
import com.karrardelivery.mapper.OrderReportMapper;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.MessageService;
import com.karrardelivery.service.OrderService;
import com.karrardelivery.controller.spec.OrderSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.karrardelivery.constant.ErrorCodes.*;
import static com.karrardelivery.constant.Messages.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TraderRepository traderRepository;
    private final OrderMapper orderMapper;
    private final MessageService messageService;
    private final OrderReportMapper orderReportMapper;

    @Override
    public GenericResponse<String> createOrder(OrderDto orderDto) {
        validateUniqueInvoice(orderDto.getInvoiceNo());
        applyDefaultValues(orderDto);

        Trader trader = getActiveTraderByIdOrThrow(orderDto.getTraderId());
        Order order = prepareOrder(orderDto, trader);

        calculateOrderAmounts(order);
        order.setCustomerPhoneNo(BeanUtilsHelper.internationalPhoneFormat(orderDto.getCustomerPhoneNo()));
        orderRepository.save(order);

        return GenericResponse.successResponseWithoutData(Messages.ORDER_CREATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<List<OrderDto>> getAllOrders(OrderSpec spec, Pageable pageable) {
        Specification<Order> specification = Specification.where(spec);
        Page<Order> orderList = orderRepository.findAll(specification, pageable);
        Page<OrderDto> result = orderMapper.mapToDtoPageable(orderList);
        return GenericResponse.successResponseWithPagination(
                result.getContent(),
                result,
                DATA_FETCHED_SUCCESSFULLY
        );
    }

    @Override
    public GenericResponse<OrderReportDtoList> getOrderReport(OrderSpec spec) {
        Specification<Order> specification = Specification.where(spec);
        List<Order> orderList = orderRepository.findAll(specification);
        List<OrderReportDto> report = orderReportMapper.toDtoList(orderList);
        OrderReportDtoList resultReport = populateOrderReportTotals(report);
        return GenericResponse.successResponse(resultReport, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<OrderReport> getOrderReportPerStatus(OrderSpec spec) {
        Specification<Order> specification = Specification.where(spec);
        List<Order> orderList = orderRepository.findAll(specification);
        List<OrderReportDto> orderListDto = orderReportMapper.toDtoList(orderList);

        OrderReport report = new OrderReport();
        report.setDeliveredOrders(getOrdersByStatus(orderListDto, EDeliveryStatus.DELIVERED));
        report.setUnderdeliveryOrders(getOrdersByStatus(orderListDto, EDeliveryStatus.UNDER_DELIVERY));
        report.setCancelledOrders(getOrdersByStatus(orderListDto, EDeliveryStatus.CANCELED));

        return GenericResponse.successResponse(report, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<OrderDto> getOrderById(Long id) {
        Order order = getOrderByIdOrThrow(id);
        OrderDto result = orderMapper.toDto(order);
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> updateOrderMetadata(Long id, OrderDto orderDto) {
        Order order = getOrderByIdOrThrow(id);
        orderMapper.mapToUpdate(order, orderDto);
        calculateOrderAmounts(order);
        orderRepository.save(order);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderByIdOrThrow(id);
        orderRepository.delete(order);
        GenericResponse.successResponseWithoutData(DATA_DELETED_SUCCESSFULLY);
    }

    private void calculateOrderAmounts(Order order) {
        Double traderAmount = order.getTraderAmount() != null ? order.getTraderAmount() : 0.0;
        Double deliveryAmount = order.getDeliveryAmount() != null ? order.getDeliveryAmount() : 0.0;
        Double agentAmount = order.getAgentAmount() != null ? order.getAgentAmount() : 0.0;

        order.setTotalAmount(traderAmount + deliveryAmount);
        order.setNetCompanyAmount(deliveryAmount - agentAmount);
    }

    @Override
    @Transactional
    public GenericResponse<String> updateOrderListStatus(UpdatedOrderStatusRequest request) {
        List<Order> orderList = orderRepository.findByInvoiceNoIn(request.getOrderList());
        if(orderList == null || orderList.isEmpty())
            throw new ResourceNotFoundException(
                    String.format(messageService.getMessage("order.not.found.err.msg"), String.join(",", request.getOrderList())),
                    ORDER_NOT_FOUND_ERR_CODE);

        EDeliveryStatus status = BeanUtilsHelper.fromString(EDeliveryStatus.class, request.getDeliveryStatus());
        if(status == null)
            throw new ResourceNotFoundException(
                    String.format(messageService.getMessage("delivery.status.not.found.err.msg"), request.getDeliveryStatus()),
                    INVALID_DELIVERY_STATUS_ERR_CODE);

        switch (status) {
            case DELIVERED:
                for (Order order : orderList) {
                    order.setDeliveryStatus(EDeliveryStatus.DELIVERED);
                    order.setDeliveryDate(new Date());
                }
                break;

            case EXCHANGED:
                for (Order order : orderList) {
                    order.setDeliveryStatus(EDeliveryStatus.EXCHANGED);
                    order.setDeliveryDate(new Date());
                }
                break;

            case PENDING:
                for (Order order : orderList) {
                    order.setDeliveryStatus(EDeliveryStatus.PENDING);
                    order.setDeliveryDate(new Date());
                }
                break;

            case UNDER_DELIVERY:
                for (Order order : orderList) {
                    order.setDeliveryStatus(EDeliveryStatus.UNDER_DELIVERY);
                }
                break;

            case CANCELED:
                for (Order order : orderList) {
                    order.setDeliveryStatus(EDeliveryStatus.CANCELED);
                    order.setDeliveryDate(new Date());
                }
                break;

            case  FAILED:
                for (Order order : orderList) {
                    order.setDeliveryStatus(EDeliveryStatus.FAILED);
                    order.setDeliveryDate(new Date());
                }
        }
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> updateOrderStatus(OrderDto request) {
        Order order = orderRepository.findByInvoiceNo(request.getInvoiceNo());
        if(order == null)
            throw new ResourceNotFoundException(
                    String.format(messageService.getMessage("order.not.found.err.msg"), String.join(",", request.getInvoiceNo())),
                    ORDER_NOT_FOUND_ERR_CODE);

        EDeliveryStatus status = BeanUtilsHelper.fromString(EDeliveryStatus.class, request.getDeliveryStatus());
        if(status == null)
            throw new ResourceNotFoundException(
                    String.format(messageService.getMessage("delivery.status.not.found.err.msg"), request.getDeliveryStatus()),
                    INVALID_DELIVERY_STATUS_ERR_CODE);

        switch (status) {
            case DELIVERED:
                order.setDeliveryStatus(EDeliveryStatus.DELIVERED);
                order.setDeliveryDate(new Date());
                break;

            case EXCHANGED:
                order.setDeliveryStatus(EDeliveryStatus.EXCHANGED);
                order.setDeliveryDate(new Date());
                break;

            case PENDING:
                order.setDeliveryStatus(EDeliveryStatus.PENDING);
                order.setDeliveryDate(new Date());
                break;

            case UNDER_DELIVERY:
                order.setDeliveryStatus(EDeliveryStatus.UNDER_DELIVERY);
                break;

            case CANCELED:
                order.setDeliveryStatus(EDeliveryStatus.CANCELED);
                order.setDeliveryDate(new Date());
                break;

            case FAILED:
                order.setDeliveryStatus(EDeliveryStatus.FAILED);
                order.setDeliveryDate(new Date());
        }
        updateAndRecalculateAmounts(order, request);
        orderRepository.save(order);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    public Order getOrderByIdOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(messageService.getMessage("order.not.found.err.msg"), id),
                        ORDER_NOT_FOUND_ERR_CODE
                ));
    }

    public Trader getActiveTraderByIdOrThrow(Long traderId) {
        return traderRepository.findByIdAndDeleted(traderId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("trader.not.found.err.msg"),
                        TRADER_NOT_FOUND_ERR_CODE
                ));
    }

    private OrderReportDtoList populateOrderReportTotals(List<OrderReportDto> orderList) {
        OrderReportDtoList result = new OrderReportDtoList();
        double grandTotal = 0;
        double totalDelivery = 0;
        double totalTrader = 0;
        long sequence = 1;

        if(orderList != null && !orderList.isEmpty()) {
            for (OrderReportDto dto : orderList) {
                dto.setSequenceNo(sequence++);
                grandTotal += dto.getTotalAmount();
                totalDelivery += dto.getDeliveryAmount();
                totalTrader += dto.getTraderAmount();
            }
            result.setTraderName(orderList.get(0).getTraderName());
        }
        result.setGrandTotalAmount(grandTotal);
        result.setTotalDeliveryAmount(totalDelivery);
        result.setTotalTraderAmount(totalTrader);
        result.setOrderList(orderList);
        return result;
    }

    private OrderReportDtoList getOrdersByStatus(List<OrderReportDto> orderList, EDeliveryStatus status) {
        List<OrderReportDto> filtered = orderList.stream()
                .filter(dto -> BeanUtilsHelper.getLocalizedEnumLabel(status).equals(dto.getDeliveryStatus()))
                .collect(Collectors.toList());

        return populateOrderReportTotals(filtered);
    }

    private void validateUniqueInvoice(String invoiceNo) {
        if (orderRepository.existsByInvoiceNo(invoiceNo)) {
            String message = String.format(
                    messageService.getMessage("duplicate.order.err.msg"),
                    invoiceNo
            );
            throw new DuplicateResourceException(message, DUPLICATE_ORDER_ERR_CODE);
        }
    }

    private void applyDefaultValues(OrderDto orderDto) {
        if (orderDto.getOrderDate() == null) {
            orderDto.setOrderDate(new Date());
        }

        if (orderDto.getDeliveryStatus() == null || orderDto.getDeliveryStatus().isBlank()) {
            orderDto.setDeliveryStatus("Under Delivery");
        }
    }

    private Order prepareOrder(OrderDto orderDto, Trader trader) {
        Order order = orderMapper.toEntity(orderDto);
        order.setTrader(trader);
        return order;
    }

    private void updateAndRecalculateAmounts(Order order, OrderDto request) {
        // Use request values if present, otherwise fallback to order's existing values
        double traderAmount = request.getTraderAmount() != null
                ? request.getTraderAmount()
                : defaultIfNull(order.getTraderAmount());

        double deliveryAmount = request.getDeliveryAmount() != null
                ? request.getDeliveryAmount()
                : defaultIfNull(order.getDeliveryAmount());

        double agentAmount = request.getAgentAmount() != null
                ? request.getAgentAmount()
                : defaultIfNull(order.getAgentAmount());

        // Only update the fields that are explicitly passed in
        if (request.getTraderAmount() != null) order.setTraderAmount(traderAmount);
        if (request.getDeliveryAmount() != null) order.setDeliveryAmount(deliveryAmount);
        if (request.getAgentAmount() != null) order.setAgentAmount(agentAmount);

        // Recalculate dependent amounts
        order.setTotalAmount(traderAmount + deliveryAmount);
        order.setNetCompanyAmount(deliveryAmount - agentAmount);
    }

    private double defaultIfNull(Double value) {
        return value != null ? value : 0.0;
    }
}
