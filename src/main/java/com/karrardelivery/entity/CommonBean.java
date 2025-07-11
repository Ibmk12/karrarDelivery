package com.karrardelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class CommonBean implements Serializable {

    private static final long serialVersionUID = -3424777851855986787L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @JsonIgnore
    private String status;

    @Column(name = "last_updated")
    private Date lastUpdate;

    @Column(name = "creation_time", updatable = false)
    private Date creationTime;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.creationTime = now;
        this.lastUpdate = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = new Date();
    }
}
