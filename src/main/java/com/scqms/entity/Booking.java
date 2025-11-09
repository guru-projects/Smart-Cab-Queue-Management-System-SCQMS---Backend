package com.scqms.entity;

import com.scqms.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "employee_id")
    private Long employeeId;  // Keep this for setting the value

    @ManyToOne
    @JoinColumn(name = "cab_id")
    private Cab cab;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
}