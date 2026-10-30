package com.karrardelivery.entity;

import com.karrardelivery.entity.enums.EOrderHistoryAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * One change made to an order (audit trail). Rows are kept even if the order is deleted,
 * so there is no foreign key to orders.
 */
@Entity
@Table(name = "order_history")
@Getter
@Setter
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "invoice_no")
    private String invoiceNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private EOrderHistoryAction action;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    /** phone number of the user (JWT subject) */
    @Column(name = "changed_by")
    private String changedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "changed_at", nullable = false)
    private Date changedAt;
}
