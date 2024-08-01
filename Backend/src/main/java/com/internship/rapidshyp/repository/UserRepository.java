package com.internship.rapidshyp.repository;

import com.internship.rapidshyp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findByUserName(String username);
}
