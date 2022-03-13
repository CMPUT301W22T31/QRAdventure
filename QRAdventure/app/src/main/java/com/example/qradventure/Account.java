package com.example.qradventure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an player account. Tracks Contact info, Player QRs, and Records the player has scanned
 * All account usernames are unique.
 */
public class Account {
    private String username;
    private String email;
    private String phoneNumber;
    private String loginQR;
    private String statusQR;
    private ArrayList<Record> myRecords; // A list is used here to enforce chronological order
    private Set<Record> alreadyHas; // Used for tracking which records the player has

    /**
     * Constructor with the record list
     * @param username
     *      Unique username
     * @param email
     * @param phoneNumber
     * @param loginQR
     *      Player QR. Logs the user in when scanned
     * @param statusQR
     *      Player QR. Shows their game status when scanned
     * @param myRecords
     *
     */
    public Account(String username, String email, String phoneNumber, String loginQR, String statusQR, ArrayList<Record> myRecords) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.loginQR = loginQR;
        this.statusQR = statusQR;
        this.myRecords = myRecords;
        this.alreadyHas = new HashSet<Record>();
    }

    /**
     * Constructor without a list of records
     * @param username
     * @param email
     * @param phoneNumber
     * @param loginQR
     * @param statusQR
     */
    public Account(String username, String email, String phoneNumber, String loginQR, String statusQR) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.loginQR = loginQR;
        this.statusQR = statusQR;
        this.myRecords = new ArrayList<Record>();
        this.alreadyHas = new HashSet<Record>();
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

    /**
     * Checks equality of two accounts
     * @param o
     *      One account we are checking
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(username, account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public boolean containsRecord(Record record){

        return alreadyHas.contains(record);

    }

    public Boolean addRecord(Record record){

        if (this.containsRecord(record)){
            return false;
        }
        alreadyHas.add(record);
        myRecords.add(record);
        return true;

    }

    public ArrayList<Record> getMyRecords() {
        return myRecords;
    }


}
