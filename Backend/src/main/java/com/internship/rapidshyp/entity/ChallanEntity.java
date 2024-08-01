package com.internship.rapidshyp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "challan")
public class ChallanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challan_no", unique = true, nullable = false)
    private String challanNo;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_no")
    private String vehicleNo;

    @ManyToOne
    @JoinColumn(name = "from_warehouse_id")
    private WareHouseEntity fromWarehouse;

    @ManyToOne
    @JoinColumn(name = "to_warehouse_id")
    private WareHouseEntity toWarehouse;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "challan")
    private List<ShipmentEntity> shipments = new ArrayList<>();
}