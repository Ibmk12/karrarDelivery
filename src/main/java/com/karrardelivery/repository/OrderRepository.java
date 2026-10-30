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
      AND ( :underDeliveryUntil IS NULL
            OR o.order_date <= :underDeliveryUntil )

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
            @Param("underDeliveryUntil") LocalDateTime underDeliveryUntil, // nullable
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
          AND ( :underDeliveryUntil IS NULL 
                OR o.order_date <= :underDeliveryUntil )

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
          AND ( :underDeliveryUntil IS NULL 
                OR o.order_date <= :underDeliveryUntil )

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
            @Param("underDeliveryUntil") LocalDateTime underDeliveryUntil,
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


    // ---------------------------------------------------------------- dashboard

    long countByOrderDateGreaterThanEqualAndOrderDateLessThan(Date from, Date to);

    long countByDeliveryStatusAndDeliveryDateGreaterThanEqualAndDeliveryDateLessThan(
            EDeliveryStatus deliveryStatus, Date from, Date to);

    long countByDeliveryStatus(EDeliveryStatus deliveryStatus);

    long countByDeliveryStatusAndOrderDateBefore(EDeliveryStatus deliveryStatus, Date date);

    /** [delivered count, sum of delivery fees, sum of company net] for orders with this status delivered in [from, to) */
    @Query("""
    SELECT COUNT(o), COALESCE(SUM(o.deliveryAmount), 0), COALESCE(SUM(o.netCompanyAmount), 0)
    FROM Order o
    WHERE o.deliveryStatus = :status
      AND o.deliveryDate >= :from
      AND o.deliveryDate < :to
""")
    List<Object[]> sumMoneyBetween(@Param("status") EDeliveryStatus status,
                                   @Param("from") Date from,
                                   @Param("to") Date to);

    /**
     * Per agent: [agentId, name, phone, assigned today, delivered today, under delivery now, cash to collect]
     * cash to collect = total amount of orders still under delivery + orders delivered today.
     */
    @Query("""
    SELECT a.id, a.name, a.phoneNumber,
           SUM(CASE WHEN o.orderDate >= :start AND o.orderDate < :end THEN 1 ELSE 0 END),
           SUM(CASE WHEN o.deliveryStatus = :delivered AND o.deliveryDate >= :start AND o.deliveryDate < :end THEN 1 ELSE 0 END),
           SUM(CASE WHEN o.deliveryStatus = :underDelivery THEN 1 ELSE 0 END),
           COALESCE(SUM(CASE WHEN o.deliveryStatus = :underDelivery
                              OR (o.deliveryStatus = :delivered AND o.deliveryDate >= :start AND o.deliveryDate < :end)
                             THEN o.totalAmount ELSE 0 END), 0)
    FROM Order o
    JOIN o.agent a
    WHERE a.deleted = false
      AND ((o.orderDate >= :start AND o.orderDate < :end)
           OR o.deliveryStatus = :underDelivery
           OR (o.deliveryStatus = :delivered AND o.deliveryDate >= :start AND o.deliveryDate < :end))
    GROUP BY a.id, a.name, a.phoneNumber
    ORDER BY a.name
""")
    List<Object[]> findAgentsActivity(@Param("start") Date start,
                                      @Param("end") Date end,
                                      @Param("delivered") EDeliveryStatus delivered,
                                      @Param("underDelivery") EDeliveryStatus underDelivery);

}
