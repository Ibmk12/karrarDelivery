package com.karrardelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "orders")  // Escaping the reserved keyword
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date orderDate;
    private String invoiceNo;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id")
    @JsonIgnore
    private Trader trader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emirate_id")
    @JsonIgnore
    private Emirate emirate;

    private String deliveryAgent;
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

}
