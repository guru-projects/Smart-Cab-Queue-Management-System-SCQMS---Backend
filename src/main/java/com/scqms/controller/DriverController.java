package com.scqms.controller;

import com.scqms.dto.LocationRequest;
import com.scqms.entity.Cab;
import com.scqms.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // ✅ Get all available cabs
    @GetMapping("/available-cabs")
    public ResponseEntity<List<Cab>> getAvailableCabs() {
        return ResponseEntity.ok(driverService.getAvailableCabs());
    }

    // ✅ Start a new session
    @PostMapping("/start-session/{cabId}")
    public ResponseEntity<?> startSession(@PathVariable Long cabId) {
        driverService.startSession(cabId);
        return ResponseEntity.ok("Session started successfully");
    }

    // ✅ End the active session
    @PostMapping("/end-session")
    public ResponseEntity<?> endSession() {
        driverService.endSession();
        return ResponseEntity.ok("Session ended successfully");
    }

    // ✅ Update live location
    @PostMapping("/update-location")
    public ResponseEntity<?> updateLocation(@RequestBody LocationRequest request) {
        driverService.updateDriverLocation(request);
        return ResponseEntity.ok("Location updated successfully");
    }

    // ✅ Get my current cab (active session)
    @GetMapping("/my-cab")
    public ResponseEntity<?> getMyCab() {
        Cab cab = driverService.getMyActiveCab();
        return ResponseEntity.ok(cab != null ? cab : "No active cab");
    }
}