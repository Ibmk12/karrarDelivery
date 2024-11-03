package com.karrardelivery.repository;

import com.karrardelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTraderIdAndOrderDateBetween(Long traderId, Date startDate, Date endDate);
}