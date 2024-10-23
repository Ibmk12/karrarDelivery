package com.karrardelivery.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class Emirate extends CommonBean{

    private String name;
    private String regionCode;
}

