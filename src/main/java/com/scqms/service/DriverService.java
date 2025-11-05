package com.scqms.service;

import com.scqms.dto.LocationRequest;
import com.scqms.entity.Cab;
import com.scqms.enums.CabStatus;
import com.scqms.repository.CabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final CabRepository cabRepository;
    private final GeoFenceUtil geoFenceUtil;

    public void updateDriverLocation(LocationRequest request) {
        Cab cab = cabRepository.findById(request.getCabId())
                .orElseThrow(() -> new RuntimeException("Cab not found"));

        cab.setLatitude(request.getLatitude());
        cab.setLongitude(request.getLongitude());
        cab.setLastUpdated(LocalDateTime.now());

        if (geoFenceUtil.isNearGuindy(request.getLatitude(), request.getLongitude())) {
            cab.setStatus(CabStatus.AVAILABLE);
        } else if (geoFenceUtil.isNearOffice(request.getLatitude(), request.getLongitude())) {
            cab.setStatus(CabStatus.BUSY);
        } else {
            // In between: keep as BUSY if previously BUSY, else OFFLINE
            if (cab.getStatus() == null) cab.setStatus(CabStatus.OFFLINE);
        }

        cabRepository.save(cab);
    }
}
