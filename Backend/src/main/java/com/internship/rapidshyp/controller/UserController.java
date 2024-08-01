package com.internship.rapidshyp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.internship.rapidshyp.entity.UserEntity;
import com.internship.rapidshyp.security.JwtUtils;
import com.internship.rapidshyp.security.UserDetailsServiceImpl;
import com.internship.rapidshyp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/public")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserEntity user) {
        try {
            //log.debug(String.valueOf(user));
            userService.createUser(user);
            return new ResponseEntity<>("{'data':''}", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity user) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Return the JSON response with OK status
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken", e);

            // Return the error JSON response with BAD_REQUEST status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect username or password");
        }
    }
}
