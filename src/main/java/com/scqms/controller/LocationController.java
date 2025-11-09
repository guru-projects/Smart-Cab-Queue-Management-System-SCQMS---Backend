package com.scqms.controller;

import com.scqms.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyLocation(@RequestBody Map<String, Object> body) {
        double lat = Double.parseDouble(body.get("lat").toString());
        double lon = Double.parseDouble(body.get("lon").toString());

        Map<String, Object> result = locationService.verify(lat, lon);
        return ResponseEntity.ok(result);
    }
}
