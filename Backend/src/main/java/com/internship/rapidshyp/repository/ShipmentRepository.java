package com.internship.rapidshyp.repository;

import com.internship.rapidshyp.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentEntity,Long> {

        List<ShipmentEntity> findByTaggedFalse();

        List<ShipmentEntity> findByTaggedFalseAndFromWarehouseId(Long warehouseId);
}
