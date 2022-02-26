package com.example.qradventure;

import android.util.Log;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * This class represents a QR code. It is itentified by the hash of the QR code it
 * represents
 */
public class QR {

    private String hash;
    private int score = 0;
    private ArrayList<Account> scannedAccounts;
    private ArrayList<String> geolocation;

    // two constructors
    public QR(String hash, int score, ArrayList<Account> scannedAccounts, ArrayList<String> geolocation) {
        this.hash = hash;
        this.score = score;
        this.scannedAccounts = scannedAccounts;
        this.geolocation = geolocation;
    }

    public QR(String QR){

        hash = DigestUtils.sha256Hex(QR);
        score = getScore(hash);
        scannedAccounts = new ArrayList<Account>();
        geolocation = new ArrayList<String>();

    }


    /**
     * Function to obtain the score from a hexidecimal hash
     * @param hash
     *          The hash we wish to get the score from
     * @return
     *          The score as an integer
     */
    public int getScore(String hash){

        char nextChar = ' ';
        char current = ' ';
        int consecutive = 0;

        int QRScore = 0;

        for (int i = 0; i < hash.length() - 1; i++){
            current = hash.charAt(i);
            nextChar = hash.charAt(i + 1);


            if (current == nextChar){
                consecutive++;
            }else{

                if (current == '0'){
                    QRScore += Math.pow(20, consecutive);
                }
                else{
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

        return QRScore;
    }


    /**
     * Getter for the hash
     * @return
     *      The hash as a string
     */
    public String getHash(){
        return this.hash;
    }


}
