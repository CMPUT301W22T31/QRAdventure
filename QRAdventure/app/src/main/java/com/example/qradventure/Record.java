package com.example.qradventure;

import java.util.ArrayList;

public class Record {

    private Account user;
    private QR theQR;
    private ArrayList<String> comments;
    // String recordID = username + "-" + QRhash; -> For Firebase document ID

    public Record(Account user, QR theQR, ArrayList<String> comments) {
        this.user = user;
        this.theQR = theQR;
        this.comments = comments;
    }

}
