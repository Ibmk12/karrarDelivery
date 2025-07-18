package com.karrardelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import com.karrardelivery.entity.enums.EEmirate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date orderDate;
    private String invoiceNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private EDeliveryStatus deliveryStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id")
    @JsonIgnore
    private Trader trader;

    @Enumerated(EnumType.STRING)
    @Column(name = "emirate", nullable = false)
    private EEmirate emirate;


    private String deliveryAgent;
    @Column(name = "delivery_date")
    private Date deliveryDate;
    private String address;
    private String longitude;
    private String latitude;
    private Double totalAmount;
    private Double traderAmount;
    private Double deliveryAmount;
    private Double agentAmount;
    private Double netCompanyAmount;
    private String customerPhoneNo;
    private String comment;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated", nullable = false)
    private Date lastUpdated;
}
