package com.example.qradventure;

import java.util.ArrayList;

/**
 * Represents an player account.
 * All account usernames are unique.
 */
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

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLoginQR() {
        return loginQR;
    }

    public String getStatusQR() {
        return statusQR;
    }
}
