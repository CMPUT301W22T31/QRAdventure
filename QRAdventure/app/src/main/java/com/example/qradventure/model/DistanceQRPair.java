package com.example.qradventure.model;

public class DistanceQRPair implements Comparable<DistanceQRPair> {

    private QR qr;
    private double distance;


    /**
     * constructor
     * @param qr The qr
     * @param distance the distance
     */
    public DistanceQRPair(QR qr, double distance){
        this.qr = qr;
        this.distance = distance;
    }

    /**
     * compares to another pair.
     *
     * @param distanceRecordPair
     * @return
     *  -1 if this is lower
     *  0 if they are equal
     *  1 if this is larger
     */
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


    public double getDistance() {
        return distance;
    }
}
