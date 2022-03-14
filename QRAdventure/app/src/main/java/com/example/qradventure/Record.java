package com.example.qradventure;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Represents a record of a QR that was scanned by a player
 */
public class Record {

    private Account user;
    private QR theQR;
    private ArrayList<String> comments;  // TODO: QR should hold comments, not the record.
    private String recordID; //-> For Firebase document ID

    /**
     * Constructor
     * @param user
     *      username of the player
     * @param theQR
     *      hash of the QR code scanned
     */
    public Record(Account user, QR theQR) {
        this.user = user;
        this.theQR = theQR;
        recordID = user.getUsername() + "-" + theQR.getHash();
    }

    /**
     * Checks for the equality of Records
     * @param o
     *      Record object comparing with
     * @return
     *      true if the objects are equal
     *      false if they are not equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(user, record.user) && Objects.equals(theQR, record.theQR);
    }

    /**
     * Gets the hash code of the user
     * @return hash code of the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, theQR);
    }

    public String getID(){
        return this.recordID;
    }
    public String getQRHash() {
        return theQR.getHash();
    }
    public int getQRscore() {
        return theQR.getScore();
    }
}
