package com.internship.rapidshyp.service;

import com.internship.rapidshyp.entity.ChallanEntity;
import com.internship.rapidshyp.entity.ShipmentEntity;
import com.internship.rapidshyp.repository.ChallanRepository;
import com.internship.rapidshyp.repository.ShipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChallanService {
    private static final Logger logger = LoggerFactory.getLogger(ChallanService.class);
    @Autowired
    private ChallanRepository challanRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Transactional
    public ChallanEntity createChallanWithShipments(ChallanEntity challan, List<Long> selectedShipmentIds) {
        logger.info("Creating challan with shipments. Challan: {}, Shipment IDs: {}", challan, selectedShipmentIds);
        if (challan == null || selectedShipmentIds == null || selectedShipmentIds.isEmpty()) {
            logger.error("Challan or shipment IDs are null or empty");
            throw new IllegalArgumentException("Challan and shipment IDs must not be null or empty");
        }
        challan = challanRepository.save(challan);  // Save the challan first to get its ID
        logger.info("Challan saved: {}", challan);

        List<ShipmentEntity> shipments = shipmentRepository.findAllById(selectedShipmentIds);
        logger.info("Found {} shipments", shipments.size());
        for (ShipmentEntity shipment : shipments) {
            shipment.setChallan(challan);
            shipment.setStatus("Dispatched");
            shipment.setTagged(true);
        }
        shipmentRepository.saveAll(shipments);
        logger.info("Updated {} shipments", shipments.size());

        return challan;  // Return the created challan
    }

    public void removeShipmentFromChallan(Long shipmentId) {
        ShipmentEntity shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found"));
        shipment.setChallan(null);
        shipment.setTagged(false);
        shipmentRepository.save(shipment);
    }

    public List<ChallanEntity> getAllChallans() {
        return challanRepository.findAllWithShipments();
    }

    public ChallanEntity getChallanById(Long id) {
        return challanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Challan not found"));
    }

}
