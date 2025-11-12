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
    private final BookingRepository bookingRepository;

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
        // ✅ Identify driver from token
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverSession session = driverSessionRepository.findActiveByDriver(driver.getId())
                .orElseThrow(() -> new RuntimeException("No active session found"));

        Cab cab = session.getCab();

        double latitude = request.getLatitude();
        double longitude = request.getLongitude();

        // ✅ Update location + timestamp
        cab.setLatitude(latitude);
        cab.setLongitude(longitude);
        cab.setLastUpdated(LocalDateTime.now());

        // ✅ Determine current location
        if (geoFenceUtil.isNearGuindy(latitude, longitude)) {
            cab.setCurrentLocation("STATION");
        } else if (geoFenceUtil.isNearOffice(latitude, longitude)) {
            cab.setCurrentLocation("OFFICE");
        } else {
            cab.setCurrentLocation("ON_ROUTE");
        }

        cabRepository.save(cab);

        System.out.println("📍 Cab " + cab.getCabNumber()
                + " | Driver: " + driver.getName()
                + " | Location: " + cab.getCurrentLocation()
                + " | Lat: " + latitude
                + " | Lon: " + longitude);
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
