package com.karrardelivery.service.impl;

import com.karrardelivery.controller.spec.OrderSpec;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.mapper.OrderReportMapper;
import com.karrardelivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReportDataService {

    private final OrderRepository orderRepository;
    private final OrderReportMapper orderReportMapper;

    public List<OrderReportDto> fetchReportData(OrderSpec spec) {
        Specification<Order> specification = Specification
                .where(spec)
                .and(OrderSpec.applyDefaultStatusesIfMissing(spec));
        List<Order> orders = orderRepository.findAll(specification);
        return orderReportMapper.toDtoList(orders);
    }
}
