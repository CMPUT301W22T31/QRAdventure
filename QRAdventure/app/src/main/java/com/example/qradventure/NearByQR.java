package com.example.qradventure;

// QR's that will be displayed in the nearby qr class
public class NearByQR { // dont want this to extend QR since it violates liskov principleee
    private Double longitude; // i do not want these on the qr class
    private Double latitude;
    private Double distance;
    private Integer score;
    private Boolean scanned;


    NearByQR(Double longitude, Double latitude, Double distance, Integer score, Boolean scanned) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
        this.score = score;
        this.scanned = scanned;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getDistance() {
        return distance;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean hasBeenScanned() {
        return scanned;
    }
}
