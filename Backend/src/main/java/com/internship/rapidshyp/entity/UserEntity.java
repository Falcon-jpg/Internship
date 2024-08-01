package com.internship.rapidshyp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "")
public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer userId;
        @NonNull
        @Column(unique = true)
        private String userName;
        @NonNull
        private String password;
        @NonNull
        private String email;
        @NonNull
        private String role;

}
