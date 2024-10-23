package com.karrardelivery.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Trader extends CommonBean{

    private String name;
    private String contactInfo;
}
