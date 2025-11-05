package com.scqms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "cab_id")
    private Cab cab;

    private LocalDateTime createdAt;
    private String status; // QUEUED, ASSIGNED, COMPLETED
}
