package com.rsd.raul.examples;

import java.math.BigDecimal;

public class Location {

    // ------------------------- ATTRIBUTES --------------------------

    private BigDecimal longitude;
    private BigDecimal latitude;
    private String timestamp;

    // ------------------------- CONSTRUCTOR -------------------------

    Location(BigDecimal longitude, BigDecimal latitude, String timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    // ---------------------- GETTERS & SETTERS ----------------------

    BigDecimal getLongitude() {
        return longitude;
    }

    BigDecimal getLatitude() {
        return latitude;
    }

    String getTimestamp() {
        return timestamp;
    }

    // -------------------------- USE CASES --------------------------

    boolean isNearby(Location location) {
        return location != null && getDistance(location) <= VehicleLocations.NEARBY_DISTANCE;
    }

    double getDistance(Location location){

        // Calculate Euclidean distance with maximum precision
        BigDecimal latDifEle = location.getLatitude().subtract(latitude).pow(2);
        BigDecimal lonDifEle = location.getLongitude().subtract(longitude).pow(2);

        double distance = Math.sqrt(latDifEle.add(lonDifEle).doubleValue()) * 1E5;
        System.out.println("Distance: " + distance);
        return distance;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Location))
            return false;

        Location location = (Location) obj;
        return latitude.compareTo(location.getLatitude()) == 0 && longitude.compareTo(location.getLongitude()) == 0;
    }

    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
