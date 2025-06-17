package com.karrardelivery.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Trader extends CommonBean{

    private String name;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "code")
    private String code;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
