package com.internship.rapidshyp.controller;

import com.internship.rapidshyp.entity.WareHouseEntity;
import com.internship.rapidshyp.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/warehouse")
public class WareHouseController {

    @Autowired
    private WareHouseService wareHouseService;

    @PostMapping()
    public ResponseEntity<?> createWarehouse(@RequestBody WareHouseEntity wareHouse){
        try{
            wareHouseService.save(wareHouse);
            return ResponseEntity.status(HttpStatus.CREATED).body(wareHouse);
        }catch(Exception e){
            log.error("Error:",e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    @GetMapping("/show")
    public ResponseEntity<List<WareHouseEntity>> showWarehouse(){
        try{
            List<WareHouseEntity> all = wareHouseService.getAll();
            if(all!= null && !all.isEmpty()){
                return new ResponseEntity<>(all, HttpStatus.OK);
            }return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

