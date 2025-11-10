package com.scqms.service;

import com.scqms.entity.Cab;
import com.scqms.enums.Status; // ✅ <-- This import is critical
import com.scqms.repository.CabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CabService {

    private final CabRepository cabRepository;

    public List<Cab> getAll() {
        return cabRepository.findAll();
    }

    public Cab getById(Long id) {
        return cabRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cab not found with ID: " + id));
    }

    public Cab updateStatus(Long cabId, String status) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("Cab not found with ID: " + cabId));

        try {
            cab.setStatus(Status.valueOf(status.toUpperCase()));  // ✅ Works now
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid cab status: " + status);
        }

        cab.setLastUpdated(LocalDateTime.now());
        return cabRepository.save(cab);
    }

    public Cab updateLocation(Long cabId, Float latitude, Float longitude) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("Cab not found with ID: " + cabId));

        cab.setLatitude(Double.valueOf(latitude));
        cab.setLongitude(Double.valueOf(longitude));
        cab.setLastUpdated(LocalDateTime.now());
        return cabRepository.save(cab);
    }

    public Cab updateLocationByDriver(Long driverId, Double latitude, Double longitude) {
        Cab cab = cabRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("No cab found for driver ID: " + driverId));

        cab.setLatitude(latitude);
        cab.setLongitude(longitude);
        cab.setLastUpdated(LocalDateTime.now());
        return cabRepository.save(cab);
    }

}
