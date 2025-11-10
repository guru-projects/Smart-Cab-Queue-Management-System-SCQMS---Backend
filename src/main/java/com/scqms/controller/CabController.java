package com.scqms.controller;

import com.scqms.entity.Cab;
import com.scqms.service.CabService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cab")
@RequiredArgsConstructor
public class CabController {

    private final CabService cabService;

    @GetMapping("/all")
    public List<Cab> getAll() {
        return cabService.getAll();
    }

    @GetMapping("/{id}")
    public Cab getById(@PathVariable Long id) {
        return cabService.getById(id);
    }

    @PutMapping("/update-status/{cabId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long cabId, @RequestParam String status) {
        return ResponseEntity.ok(cabService.updateStatus(cabId, status));
    }

    @PutMapping("/update-location/{cabId}")
    public ResponseEntity<?> updateLocation(@PathVariable Long cabId,
                                            @RequestParam Float latitude,
                                            @RequestParam Float longitude) {
        return ResponseEntity.ok(cabService.updateLocation(cabId, latitude, longitude));
    }

    @PutMapping("/update-location/driver/{driverId}")
    public ResponseEntity<?> updateLocationByDriver(
            @PathVariable Long driverId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        return ResponseEntity.ok(cabService.updateLocationByDriver(driverId, latitude, longitude));
    }

}
