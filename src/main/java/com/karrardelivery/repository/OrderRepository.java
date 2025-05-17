package com.karrardelivery.repository;

import com.karrardelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> , JpaSpecificationExecutor<Order> {

    boolean existsByInvoiceNo(String invoiceNo);
    List<Order> findByTraderIdAndOrderDateBetween(Long traderId, Date startDate, Date endDate);
}