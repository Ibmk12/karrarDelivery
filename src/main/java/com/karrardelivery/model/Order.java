package com.karrardelivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    private double totalAmount;
    private double traderAmount;
    private double deliveryAmount;
    private double agentAmount;
    private double netCompanyAmount;
    private String customerPhoneNo;
    private String comment;

}
