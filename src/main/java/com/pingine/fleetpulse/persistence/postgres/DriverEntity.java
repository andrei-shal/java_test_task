package com.pingine.fleetpulse.persistence.postgres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class DriverEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    @Column(name = "email", nullable = false, length = 128)
    private String email;
}
