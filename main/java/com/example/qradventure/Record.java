package com.example.qradventure;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Represents a record of a QR that was scanned by a player
 *
 */
public class Record {

    private Account user;
    private QR theQR;
    private ArrayList<String> comments;  // TODO: QR should hold comments, not the record.
    private String recordID; //-> For Firebase document ID

    public Record(Account user, QR theQR) {
        this.user = user;
        this.theQR = theQR;
        recordID = user.getUsername() + "-" + theQR.getHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(user, record.user) && Objects.equals(theQR, record.theQR);
    }
    String getID(){
        return this.recordID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, theQR);
    }

    public String getQRHash() {
        return theQR.getHash();
    }
    public int getQRscore() {
        return theQR.getScore();
    }
}
