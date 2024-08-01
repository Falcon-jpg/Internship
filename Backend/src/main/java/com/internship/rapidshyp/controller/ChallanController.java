package com.internship.rapidshyp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.rapidshyp.entity.ChallanEntity;
import com.internship.rapidshyp.entity.ShipmentEntity;
import com.internship.rapidshyp.service.ChallanService;
import com.internship.rapidshyp.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/challans")
public class ChallanController {

    private static final Logger logger = LoggerFactory.getLogger(ChallanController.class);

    @Autowired
    private ChallanService challanService;

    @Autowired
    private ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<?> createChallan(@RequestBody ChallanEntity challan,
                                           @RequestParam List<Long> shipmentIds) {
        logger.info("Received request to create challan: {}", challan);
        logger.info("Shipment IDs: {}", shipmentIds);
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info("Raw JSON received: {}", mapper.writeValueAsString(challan));

            ChallanEntity createdChallan = challanService.createChallanWithShipments(challan, shipmentIds);
            logger.info("Challan created successfully: {}", createdChallan);
            return new ResponseEntity<>(createdChallan, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for creating challan", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating challan", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{challanId}/shipments/{shipmentId}")
    public ResponseEntity<Void> removeShipmentFromChallan(@PathVariable Long challanId,
                                                          @PathVariable Long shipmentId) {
        challanService.removeShipmentFromChallan(shipmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ChallanEntity>> getAllChallans() {
        List<ChallanEntity> challans = challanService.getAllChallans();
        return new ResponseEntity<>(challans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallanEntity> getChallanById(@PathVariable Long id) {
        ChallanEntity challan = challanService.getChallanById(id);
        return new ResponseEntity<>(challan, HttpStatus.OK);
    }

    @GetMapping("/untagged-shipments")
    public ResponseEntity<List<ShipmentEntity>> getUntaggedShipments(@RequestParam(required = false) Long warehouseId) {
        List<ShipmentEntity> untaggedShipments = shipmentService.getUntaggedShipments(warehouseId);
        return new ResponseEntity<>(untaggedShipments, HttpStatus.OK);
    }
}
