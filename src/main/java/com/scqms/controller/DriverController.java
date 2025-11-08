package com.scqms.controller;

import com.scqms.dto.LocationRequest;
import com.scqms.entity.Cab;
import com.scqms.entity.Driver;
import com.scqms.repository.DriverRepository;
import com.scqms.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final DriverRepository driverRepository;

    // ✅ Update driver location
    @PostMapping("/update-location")
    public ResponseEntity<?> updateLocation(@RequestBody LocationRequest request) {
        driverService.updateDriverLocation(request);
        return ResponseEntity.ok("Location updated successfully");
    }

    // ✅ Get my assigned cab (based on mobile in JWT)
    @GetMapping("/my-cab")
    public ResponseEntity<?> myCab(Authentication auth) {
        // When driver logs in, JWT subject = mobile number
        String mobile = auth.getName();

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found for mobile: " + mobile));

        Cab cab = driver.getCab();

        return ResponseEntity.ok(Map.of("cab", cab));
    }
}