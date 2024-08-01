package com.internship.rapidshyp.service;

import com.internship.rapidshyp.entity.WareHouseEntity;
import com.internship.rapidshyp.repository.WareHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WareHouseService {

    @Autowired
    private WareHouseRepository wareHouseRepository;

    public void save(WareHouseEntity wareHouse) {
        wareHouseRepository.save(wareHouse);
    }

    public List<WareHouseEntity> getAll() {
       return wareHouseRepository.findAll();
    }
}
