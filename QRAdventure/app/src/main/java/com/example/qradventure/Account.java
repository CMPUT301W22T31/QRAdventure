package com.example.qradventure;

import java.lang.reflect.Array;
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
    private ArrayList<Double> location; // 0 index is longitude, 1 is latitude
    private ArrayList<Record> myRecords; // A list is used here to enforce chronological order
    //private Set<Record> alreadyHas; // Used for tracking which records the player has

    /**
     * Constructor with the record list
     * @param username
     *      Unique username
     * @param email
     *      Email entered by the user
     * @param phoneNumber
     *      Phone number entered by the user
     * @param loginQR
     *      Player QR. Logs the user in when scanned
     * @param statusQR
     *      Player QR. Shows their game status when scanned
     * @param myRecords
     *      Collection of Records that this user has scanned
     */
    public Account(String username, String email, String phoneNumber, String loginQR, String statusQR, ArrayList<Record> myRecords) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.loginQR = loginQR;
        this.statusQR = statusQR;
        this.myRecords = myRecords;
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
        this.location = new ArrayList<Double>();
        location.add(-113.506660);
        location.add(53.533172);

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

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }

    /**
     * Checks equality of two accounts
     * @param o
     *      The object we are comparing the account with
     * @return true if the two accounts are equal
     *      false if the two accounts are not equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(username, account.username);
    }

    /**
     * Returns the hash code of the account with the username
     * @return hash code of the account with the username
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Checks if the account already scanned this QR code
     * @param record
     *      Information of user and QR as a record
     * @return true if the account contains a record
     *      false if the account does not contain a record
     */
    public boolean containsRecord(Record record){

        for (Record r: myRecords){
            if (record.equals(r)){
                return true;
            }
        }
        return false;

    }

    /**
     * Add a Record to the Account and an ArrayList of records
     * @param record
     * @return true if addition was successful
     *      false if the Account already conta                        test = ined the record
     */
    public Boolean addRecord(Record record){

        if (this.containsRecord(record)){
            return false;
        }
        myRecords.add(record);
        return true;

    }

    /**
     * Removes a record from this account
     * @param hash of the record we are removing
     */
    public void removeRecord(String hash){

        int i = 0;
        for (Record r: myRecords){
            if (r.getQRHash() == hash){
                myRecords.remove(i);
                return;
            }
            i++;

        }

    }


    /**
     * Get the total sum of scores of the Account
     * @return sum of scores
     */
    public int getTotalScore(){
        int sum = 0;
        for (Record record: myRecords){
            sum += record.getQRscore();
        }
        return sum;
    }

    /**
     * Gets the QR with the lowest score
     * @return
     *      The lowest score
     */
    public int getLowestQR() {

        if (myRecords.size() == 0){
            return 0;
        }

        // TODO: verify this works. Sometimes records are not present.
        int smallest = myRecords.get(0).getQRscore();
        for (Record record: myRecords
        ) {
            if (record.getQRscore() < smallest){
                smallest = record.getQRscore();
            }
        }
        return smallest;
    }


    /**
     * Gets the QR with the highest score
     * @return
     *      The Highest score
     */
    public int getHighestQR() {

        if (myRecords.size() == 0){
            return 0;
        }

        int biggest = myRecords.get(0).getQRscore();
        for (Record record: myRecords
        ) {
            if (record.getQRscore() > biggest){
                biggest = record.getQRscore();
            }
        }
        return biggest;
    }


    public int getTotalCodesScanned(){
        return myRecords.size();
    }

    public ArrayList<Record> getMyRecords() {
        return myRecords;
    }

}
