package com.scqms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String mobile;
    private String password;
    private String role;

    // ✅ Removed cabNumber field - get it from cab.getCabNumber() instead

    @OneToOne
    @JoinColumn(name = "cab_id")
    @JsonBackReference
    private Cab cab;


    // ✅ Helper method to get cab number
    public String getCabNumber() {
        return cab != null ? cab.getCabNumber() : null;
    }
}