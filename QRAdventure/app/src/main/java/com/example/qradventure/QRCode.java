package com.example.qradventure;

import java.util.ArrayList;

public class QRCode {

    int score;
    String latitude, longitude; // To be changed based on geolocation method
    ArrayList geolocation;
    ArrayList scannedBy;

    public QRCode(int score, ArrayList geolocation, ArrayList scannedBy) {
        this.score = score;
        this.geolocation = geolocation;
        this.scannedBy = scannedBy;
    }

}
