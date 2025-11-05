package com.scqms.entity;

import com.scqms.enums.CabStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cabNumber;
    private String driverName;

    @Enumerated(EnumType.STRING)
    private CabStatus status = CabStatus.AVAILABLE;

    private Double latitude;
    private Double longitude;

    private LocalDateTime lastUpdated;
}
