package com.scqms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    // In production this should be encoded
    private String password;
    private String mobile;
    private String cabNumber;
    private String role;

    @OneToOne
    @JoinColumn(name = "cab_id")
    private Cab cab;

}
