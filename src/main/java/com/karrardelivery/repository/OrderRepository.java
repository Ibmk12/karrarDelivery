package com.karrardelivery.repository;

import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> , JpaSpecificationExecutor<Order> {

    boolean existsByInvoiceNo(String invoiceNo);
    Order findByInvoiceNo(String invoiceNo);
    List<Order> findByTraderIdAndOrderDateBetween(Long traderId, Date startDate, Date endDate);
    List<Order> findByInvoiceNoIn(List<String> invoiceNos);

    @Query(
            value = """
    SELECT o.*
    FROM orders o
    JOIN trader t ON o.trader_id = t.id
    WHERE o.delivery_status = 'UNDER_DELIVERY'
      AND t.name = :traderName

    UNION ALL

    SELECT o.*
    FROM orders o
    JOIN trader t ON o.trader_id = t.id
    WHERE o.delivery_status != 'UNDER_DELIVERY'
      AND t.name = :traderName
      AND o.delivery_date >= :startOfDay
      AND o.delivery_date <= :endOfDay
    """,
            nativeQuery = true
    )
    List<Order> findCustomOrders(
            @Param("traderName") String traderName,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query(
            value = """
        SELECT * FROM (
            SELECT o.*, o.order_date AS orderDateAlias
            FROM orders o
            JOIN trader t ON o.trader_id = t.id
            WHERE o.delivery_status = 'UNDER_DELIVERY'
              AND t.name = :traderName

            UNION ALL

            SELECT o.*, o.order_date AS orderDateAlias
            FROM orders o
            JOIN trader t ON o.trader_id = t.id
            WHERE o.delivery_status != 'UNDER_DELIVERY'
              AND t.name = :traderName
              AND o.delivery_date >= :startOfDay
              AND o.delivery_date <= :endOfDay
        ) AS combined_orders
        """,
            countQuery = """
        SELECT COUNT(*) FROM (
            SELECT o.id
            FROM orders o
            JOIN trader t ON o.trader_id = t.id
            WHERE o.delivery_status = 'UNDER_DELIVERY'
              AND t.name = :traderName

            UNION ALL

            SELECT o.id
            FROM orders o
            JOIN trader t ON o.trader_id = t.id
            WHERE o.delivery_status != 'UNDER_DELIVERY'
              AND t.name = :traderName
              AND o.delivery_date >= :startOfDay
              AND o.delivery_date <= :endOfDay
        ) AS count_query
        """,
            nativeQuery = true
    )
    Page<Order> findCustomOrders(
            @Param("traderName") String traderName,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable
    );

    Page<Order> findByDeliveryStatusAndOrderDateBefore(EDeliveryStatus deliveryStatus, Date date, Pageable pageable);

    @Query("""
    SELECT FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m') AS month, COUNT(o) 
    FROM Order o
    WHERE o.orderDate >= :startDate
      AND (:status IS NULL OR o.deliveryStatus = :status)
    GROUP BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m')
    ORDER BY month DESC
""")
    List<Object[]> findOrdersCountPerMonth(@Param("startDate") Date startDate,
                                           @Param("status") EDeliveryStatus status);

}