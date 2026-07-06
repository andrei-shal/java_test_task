package com.pingine.fleetpulse.persistence.postgres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class VehicleEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "license_plate", nullable = false, length = 32)
    private String licensePlate;

    @Column(name = "model", nullable = false, length = 64)
    private String model;

    @Column(name = "vin", nullable = false, length = 32, unique = true)
    private String vin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private DriverEntity driver;
}
