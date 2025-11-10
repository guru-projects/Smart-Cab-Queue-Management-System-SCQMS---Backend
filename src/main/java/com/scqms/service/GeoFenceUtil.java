package com.scqms.service;

import org.springframework.stereotype.Component;

@Component
public class GeoFenceUtil {

    // ✅ Reference coordinates
    private static final double GUINDY_LAT = 13.0109;
    private static final double GUINDY_LON = 80.2120;

    private static final double OFFICE_LAT = 13.0050;
    private static final double OFFICE_LON = 80.2200;

    // ✅ Allowed radius (in meters)
    private static final double RANGE_METERS = 250.0;

    // ✅ Check near Guindy
    public boolean isNearGuindy(double lat, double lon) {
        return distance(lat, lon, GUINDY_LAT, GUINDY_LON) <= RANGE_METERS;
    }

    // ✅ Check near Office
    public boolean isNearOffice(double lat, double lon) {
        return distance(lat, lon, OFFICE_LAT, OFFICE_LON) <= RANGE_METERS;
    }

    // ✅ General haversine formula
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // Earth radius (meters)
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ✅ Check if within allowed geofence (Guindy OR Office)
    public boolean isWithinArea(double latitude, double longitude) {
        return isNearGuindy(latitude, longitude) || isNearOffice(latitude, longitude);
    }
}
