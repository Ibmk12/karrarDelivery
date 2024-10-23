package com.karrardelivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class CommonBean implements Serializable {

    /**
     *
     */

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

    @Column(name = "creation_time")
    private Date creationTime;

    public CommonBean() {
        super();
    }

    //TODO compare this approach with spring jpa auditing; ZonedDateTime
    @PrePersist
    public void prePersist() {
        this.creationTime = new Date();
        this.lastUpdate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdate = new Date();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "CommonBean{" +
                "id=" + id +
                ", version=" + version +
                ", status='" + status + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", creationTime=" + creationTime +
                '}';
    }
}

