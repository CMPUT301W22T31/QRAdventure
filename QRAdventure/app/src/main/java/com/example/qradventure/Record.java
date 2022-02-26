package com.example.qradventure;

public class Record {

    String username;
    String QRhash;
    String comments;
    String recordID = username + "-" + QRhash;

    public Record(String username, String QRhash, String comments) {
        this.username = username;
        this.QRhash = QRhash;
        this.comments = comments;
    }

}
