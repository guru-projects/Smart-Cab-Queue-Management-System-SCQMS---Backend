package com.scqms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.scqms.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cab")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cabNumber;
    private String currentLocation; // e.g. "STATION", "OFFICE", or "ON_ROUTE"

    @Enumerated(EnumType.STRING)
    private Status status;

    private Double latitude;
    private Double longitude;
    private LocalDateTime lastUpdated;

    @OneToOne(mappedBy = "cab", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Driver driver;
}
