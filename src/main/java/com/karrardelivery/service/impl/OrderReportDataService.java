package com.karrardelivery.service.impl;

import com.karrardelivery.common.utility.BeanUtilsHelper;
import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.mapper.OrderMapper;
import com.karrardelivery.mapper.OrderReportMapper;
import com.karrardelivery.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.karrardelivery.constant.Messages.DATA_FETCHED_SUCCESSFULLY;

@Service
@RequiredArgsConstructor
public class OrderReportDataService {

    private final OrderRepository orderRepository;
    private final OrderReportMapper orderReportMapper;
    private final HttpServletRequest request;
    private final OrderMapper orderMapper;

    public List<OrderReportDto> fetchReportData(OrderSpec spec) {
        LocalDateTime[] deliveryRange = extractDeliveryRange();
        String traderName = extractTraderName();

        List<Order> orders = orderRepository.findCustomOrders(
                traderName, deliveryRange[0], deliveryRange[1]);

        List<OrderReportDto> dtos = orderReportMapper.toDtoList(orders);
        setOrderDateIfNotEmpty(dtos, deliveryRange[0]);

        return dtos;
    }

    public GenericResponse<List<OrderReportDto>> getDailyReport(OrderSpec spec, Pageable pageable) {
        LocalDateTime[] deliveryRange = extractDeliveryRange();
        String traderName = extractTraderName();

        Page<Order> ordersPage = orderRepository.findCustomOrders(
                traderName, deliveryRange[0], deliveryRange[1], pageable);

        Page<OrderReportDto> dtoPage = orderReportMapper.mapToDtoPageable(ordersPage);
        List<OrderReportDto> dtoList = dtoPage.getContent();
        setOrderDateIfNotEmpty(dtoList, deliveryRange[0]);

        return GenericResponse.successResponseWithPagination(dtoList, dtoPage, DATA_FETCHED_SUCCESSFULLY);
    }

    private LocalDateTime[] extractDeliveryRange() {
        return BeanUtilsHelper.getDeliveryDateRange(request);
    }

    private String extractTraderName() {
        String traderName = request.getParameter("traderName");
        if (traderName == null || traderName.isBlank()) {
            throw new IllegalArgumentException("Trader name is required");
        }
        return traderName;
    }

    private void setOrderDateIfNotEmpty(List<OrderReportDto> dtos, LocalDateTime startOfDay) {
        if (dtos != null && !dtos.isEmpty()) {
            dtos.get(0).setOrderDate(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    public List<OrderDto> fetchOrderListData(OrderSpec spec) {
        Specification<Order> specification = Specification.where(spec);
        List<Order> orderList = orderRepository.findAll(specification);
        return orderMapper.toDtoList(orderList);
    }
}