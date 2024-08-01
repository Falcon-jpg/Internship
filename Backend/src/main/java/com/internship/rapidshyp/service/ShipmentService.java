package com.internship.rapidshyp.service;

import com.internship.rapidshyp.entity.ShipmentEntity;
import com.internship.rapidshyp.entity.WareHouseEntity;
import com.internship.rapidshyp.repository.ShipmentRepository;
import com.internship.rapidshyp.repository.WareHouseRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    public void save(ShipmentEntity shipment) {
        shipment.setStatus("Created");
        shipmentRepository.save(shipment);
    }

    public List<ShipmentEntity> getAll() {
        return shipmentRepository.findAll();
    }

    public List<ShipmentEntity> getUntaggedShipments(Long warehouseId) {
        if (warehouseId == null) {
            return shipmentRepository.findByTaggedFalse();
        } else {
            return shipmentRepository.findByTaggedFalseAndFromWarehouseId(warehouseId);
        }
    }

}
