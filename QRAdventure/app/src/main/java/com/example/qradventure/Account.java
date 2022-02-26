package com.example.qradventure;

import java.util.ArrayList;

public class Account {

    String username;
    String email;
    String phoneNumber;
    String loginQR;
    String statusQR;
    ArrayList<Record> myRecords;

    public Account(String username, String email, String phoneNumber, String loginQR, String statusQR, ArrayList<Record> myRecords) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.loginQR = loginQR;
        this.statusQR = statusQR;
        this.myRecords = myRecords;
    }
}
