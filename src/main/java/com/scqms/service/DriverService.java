package com.scqms.service;

import com.scqms.dto.LocationRequest;
import com.scqms.entity.Cab;
import com.scqms.entity.Driver;
import com.scqms.enums.Status;
import com.scqms.repository.CabRepository;
import com.scqms.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final CabRepository cabRepository;
    private final GeoFenceUtil geoFenceUtil;
    private final DriverRepository driverRepository;

    public void updateDriverLocation(LocationRequest request) {
        // ✅ Get logged-in driver's mobile from JWT
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ Find driver from DB
        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found for mobile: " + mobile));

        // ✅ Ensure driver has an assigned cab
        Cab cab = driver.getCab();
        if (cab == null) {
            throw new RuntimeException("Driver has no assigned cab");
        }

        // ✅ Update cab's location and timestamp
        cab.setLatitude(request.getLatitude());
        cab.setLongitude(request.getLongitude());
        cab.setLastUpdated(LocalDateTime.now());

        // ✅ Optional: check if cab entered/exited geofence
        if (geoFenceUtil != null) {
            boolean insideFence = geoFenceUtil.isWithinArea(request.getLatitude(), request.getLongitude());
            if (!insideFence) {
                cab.setStatus(Status.OFFLINE); // mark as offline if outside area
            } else {
                cab.setStatus(Status.AVAILABLE);
            }
        }

        // ✅ Save updated cab
        cabRepository.save(cab);

        System.out.println("📍 Updated location for cab " + cab.getCabNumber() +
                " | Lat: " + request.getLatitude() +
                " | Lng: " + request.getLongitude());
    }
}
