package com.scqms.service;

import com.scqms.dto.LocationRequest;
import com.scqms.entity.*;
import com.scqms.enums.Status;
import com.scqms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final CabRepository cabRepository;
    private final DriverRepository driverRepository;
    private final DriverSessionRepository driverSessionRepository;
    private final GeoFenceUtil geoFenceUtil;

    // ✅ Get all available cabs
    public List<Cab> getAvailableCabs() {
        return cabRepository.findByStatus(Status.AVAILABLE);
    }

    // ✅ Start session for a driver with a chosen cab
    public void startSession(Long cabId) {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("Cab not found"));

        if (cab.getStatus() != Status.AVAILABLE) {
            throw new RuntimeException("Cab not available!");
        }

        cab.setStatus(Status.IN_USE);
        cabRepository.save(cab);

        DriverSession session = DriverSession.builder()
                .driver(driver)
                .cab(cab)
                .startTime(LocalDateTime.now())
                .status(Status.IN_USE)
                .build();

        driverSessionRepository.save(session);
        System.out.println("🚗 Session started for cab: " + cab.getCabNumber());
    }

    // ✅ End session
    public void endSession() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverSession session = driverSessionRepository.findActiveByDriver(driver.getId())
                .orElseThrow(() -> new RuntimeException("No active session found"));

        session.setEndTime(LocalDateTime.now());
        session.setStatus(Status.AVAILABLE);
        driverSessionRepository.save(session);

        Cab cab = session.getCab();
        cab.setStatus(Status.AVAILABLE);
        cabRepository.save(cab);

        System.out.println("🛑 Session ended for cab: " + cab.getCabNumber());
    }

    // ✅ Update live location
    public void updateDriverLocation(LocationRequest request) {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverSession session = driverSessionRepository.findActiveByDriver(driver.getId())
                .orElse(null);

        Cab cab;
        if (session != null) {
            cab = session.getCab();
        } else {
            throw new RuntimeException("No cab linked to active session for this driver");
        }

        // ✅ Update location fields
        cab.setLatitude(request.getLatitude());
        cab.setLongitude(request.getLongitude());
        cab.setLastUpdated(LocalDateTime.now());

        // ✅ Status logic — keep IN_USE if session active
        if (session != null && session.getStatus() == Status.IN_USE) {
            cab.setStatus(Status.IN_USE);
        } else {
            // Session not active → check geofence for available/offline
            boolean insideFence = geoFenceUtil.isWithinArea(request.getLatitude(), request.getLongitude());
            cab.setStatus(insideFence ? Status.AVAILABLE : Status.OFFLINE);
        }

        cabRepository.save(cab);

        System.out.println("📍 Updated cab location: " + cab.getCabNumber() +
                " | Lat: " + request.getLatitude() +
                " | Lng: " + request.getLongitude());
    }


    // ✅ Get my active cab
    public Cab getMyActiveCab() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return driverSessionRepository.findActiveByDriver(driver.getId())
                .map(DriverSession::getCab)
                .orElse(null);
    }
}
