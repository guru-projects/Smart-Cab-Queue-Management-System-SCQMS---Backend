package com.scqms.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class LocationService {

    private static final double OFFICE_LAT = 13.0065;
    private static final double OFFICE_LON = 80.2208;
    private static final double STATION_LAT = 13.0108;
    private static final double STATION_LON = 80.2200;

    private static final double OFFICE_RADIUS = 150;  // meters
    private static final double STATION_RADIUS = 250; // meters

    public Map<String, Object> verify(double lat, double lon) {
        double officeDist = distance(lat, lon, OFFICE_LAT, OFFICE_LON);
        double stationDist = distance(lat, lon, STATION_LAT, STATION_LON);

        if (officeDist <= OFFICE_RADIUS)
            return Map.of("allowed", true, "zone", "OFFICE", "distance", officeDist);
        else if (stationDist <= STATION_RADIUS)
            return Map.of("allowed", true, "zone", "STATION", "distance", stationDist);
        else
            return Map.of("allowed", false, "zone", "OUTSIDE", "distance", Math.min(officeDist, stationDist));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // Earth radius in meters
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2 - lat1);
        double Δλ = Math.toRadians(lon2 - lon1);

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2)
                + Math.cos(φ1) * Math.cos(φ2)
                * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
