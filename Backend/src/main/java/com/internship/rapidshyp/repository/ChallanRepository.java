package com.internship.rapidshyp.repository;

import com.internship.rapidshyp.entity.ChallanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallanRepository extends JpaRepository<ChallanEntity, Long> {

    ChallanEntity findByChallanNo(String challanNo);

    @Query("SELECT c FROM ChallanEntity c LEFT JOIN FETCH c.shipments")
    List<ChallanEntity> findAllWithShipments();
}