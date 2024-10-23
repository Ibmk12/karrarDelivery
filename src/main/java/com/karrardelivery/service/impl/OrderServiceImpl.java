package com.karrardelivery.service.impl;

import com.karrardelivery.common.utility.Constants;
import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.model.Order;
import com.karrardelivery.repository.EmirateRepository;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        order.setInvoiceNo(orderDto.getInvoiceNo());
        order.setDeliveryAgent(orderDto.getDeliveryAgent());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setTraderAmount(orderDto.getTraderAmount());
        order.setDeliveryAmount(orderDto.getDeliveryAmount());
        order.setAgentAmount(orderDto.getAgentAmount());
        order.setNetCompanyAmount(orderDto.getNetCompanyAmount());
        order.setCustomerPhoneNo(orderDto.getCustomerPhoneNo());
        order.setComment(orderDto.getComment());
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}
