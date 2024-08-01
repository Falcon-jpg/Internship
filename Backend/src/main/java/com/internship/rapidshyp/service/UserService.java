package com.internship.rapidshyp.service;

import com.internship.rapidshyp.entity.UserEntity;
import com.internship.rapidshyp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void createUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set the default role for the user
        user.setRole("USER");
        userRepository.save(user);
    }

    public void saveUser(UserEntity user){
        userRepository.save(user);
    }
}
