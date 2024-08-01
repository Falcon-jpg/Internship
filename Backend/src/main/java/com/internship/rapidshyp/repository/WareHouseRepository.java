package com.internship.rapidshyp.repository;

import com.internship.rapidshyp.entity.WareHouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouseEntity,Long> {
}
