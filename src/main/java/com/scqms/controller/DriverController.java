package com.scqms.controller;

import com.scqms.dto.LocationRequest;
import com.scqms.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/update-location")
    public ResponseEntity<?> updateLocation(@RequestBody LocationRequest request) {
        driverService.updateDriverLocation(request);
        return ResponseEntity.ok("Location updated");
    }
}
