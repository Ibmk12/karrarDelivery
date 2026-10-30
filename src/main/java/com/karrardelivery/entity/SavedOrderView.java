package com.karrardelivery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/** A filter combination a user saved as a tab on the Orders page. */
@Entity
@Table(name = "saved_order_view")
@Getter
@Setter
public class SavedOrderView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** owner: phone number of the user (JWT subject) */
    @Column(name = "user_phone", nullable = false)
    private String userPhone;

    @Column(name = "name", nullable = false)
    private String name;

    /** the filters as JSON, e.g. {"deliveryStatus":"UNDER_DELIVERY","traderName":"Noor Store"} */
    @Column(name = "filters", nullable = false, columnDefinition = "TEXT")
    private String filters;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
