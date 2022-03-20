package com.example.qradventure;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents a QR code. Uniquely Identified by its hash string.
 */
public class QR {
    private final String hash;
    private int score = 0;
    private ArrayList<Account> scannedAccounts;
    private ArrayList<String> geolocation; // first index is longitude, second is latitude
    private ArrayList<Comment> comments;

    /**
     * More detailed constructor
     * @param hash Unique hash
     * @param score
     * @param scannedAccounts all accounts which have scanned this QR code
     * @param geolocation unused for now
     */
    public QR(String hash, int score, ArrayList<Account> scannedAccounts, ArrayList<String> geolocation) {
        this.hash = hash;
        this.score = score;
        this.scannedAccounts = scannedAccounts;
        this.geolocation = geolocation;
    }

    /**
     * Lest detailed constructor, with just the QR String
     * @param QR
     *      The String of the QR code
     */
    public QR(String QR){
        hash = new String(Hex.encodeHex(DigestUtils.md5(QR)));
        calculateScore(hash);
        scannedAccounts = new ArrayList<Account>();
        geolocation = new ArrayList<String>();
    }

    /**
     * Use for comparing QR codes
     * @param o
     *      QR code we are  comparing to
     * @return
     *      True if they are equal, False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QR qr = (QR) o;
        return Objects.equals(hash, qr.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    /**
     * Function to obtain the score from a hexidecimal hash. Uses the method suggested on Eclass
     * @param hash
     *      The hash we wish to get the score from
     * @return
     *      The score as an integer
     */
    public int calculateScore(String hash){

        char nextChar = ' ';
        char current = ' ';
        int consecutive = 0;

        int QRScore = 0;

        // Loop the entire string
        for (int i = 0; i < hash.length() - 1; i++){
            current = hash.charAt(i);
            nextChar = hash.charAt(i + 1);

            // Check if the characters are consecutive or not
            if (current == nextChar){
                consecutive++;
            }else {
                if (current == '0') {
                    QRScore += Math.pow(20, consecutive);
                } else {
                    QRScore += Math.pow(Character.digit(current, 16), consecutive);
                }
                consecutive = 0;
            }
        }

        // handle last character
        if (consecutive == 0){
            QRScore++;
        }else{
            if (current == '0'){
                QRScore += Math.pow(20, consecutive);
            }
            else{
                QRScore += Math.pow(Character.digit(current, 16), consecutive);
            }
        }

        score = QRScore;
        return score;
    }
    public void setGeolocation(ArrayList<String>geolocation) {
        this.geolocation = geolocation;
    }

    public ArrayList<String> getGeolocation() {
        return geolocation;
    }

    public int getScore() {
        return this.score;
    }

    public String getHash(){
        return this.hash;
    }


}
