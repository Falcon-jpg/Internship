package com.internship.rapidshyp.controller;

import com.internship.rapidshyp.entity.ShipmentEntity;
import com.internship.rapidshyp.entity.WareHouseEntity;
import com.internship.rapidshyp.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @PostMapping()
    public ResponseEntity<?> createWarehouse(@RequestBody ShipmentEntity shipment){
        try{
            shipmentService.save(shipment);
            return ResponseEntity.status(HttpStatus.CREATED).body("");
        }catch(Exception e){
            log.error("Error:",e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    @GetMapping("/show")
    public ResponseEntity<List<ShipmentEntity>> showWarehouse(){
        try{
            List<ShipmentEntity> all = shipmentService.getAll();
            if(all!= null && !all.isEmpty()){
                return new ResponseEntity<>(all, HttpStatus.OK);
            }return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
