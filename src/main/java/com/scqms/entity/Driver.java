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
    private String username;

    // In production this should be encoded
    private String password;

    @OneToOne
    @JoinColumn(name = "cab_id")
    private Cab cab;
}
