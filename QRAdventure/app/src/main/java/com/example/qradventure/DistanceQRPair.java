package com.example.qradventure;

public class DistanceQRPair implements Comparable<DistanceQRPair> {

    private QR qr;
    private double distance;



    public DistanceQRPair(QR qr, double distance){
        this.qr = qr;
        this.distance = distance;
    }

    @Override
    public int compareTo(DistanceQRPair distanceRecordPair) {
        double result = this.distance - distanceRecordPair.distance;

        if (result < 0){
            return -1;
        }else if (result > 0){
            return 1;
        }else{
            return 0;
        }

    }
}
