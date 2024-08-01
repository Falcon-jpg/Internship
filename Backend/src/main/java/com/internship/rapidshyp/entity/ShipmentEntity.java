package com.internship.rapidshyp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Shipment")
public class ShipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private Integer height;
    @NonNull
    private Integer width;
    @NonNull
    private Integer length;

    @ManyToOne
    @JoinColumn(name = "from_warehouse", nullable = false)
    private WareHouseEntity fromWarehouse;

    @ManyToOne
    @JoinColumn(name = "to_warehouse", nullable = false)
    private WareHouseEntity toWarehouse;

    @NonNull
    private String status;

    @JsonProperty("fragile")
    @Column(name = "is_fragile")
    private boolean fragile;

    @Column(name = "is_tagged", nullable = false)
    private boolean tagged = false;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "challan_id", nullable = true)
    private ChallanEntity challan;

}
