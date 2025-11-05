package com.scqms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String username;

    private String password;

    // role: ADMIN / EMPLOYEE / DRIVER (drivers are stored separately but role included for flexibility)
    private String role;
}
