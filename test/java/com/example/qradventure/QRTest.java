package com.example.qradventure;



import android.util.Log;

import static org.junit.Assert.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class QRTest {

    private QR testQR;
    private String testStr;


    @Before
    public void setUp(){
        testStr = "wikipedia.org";
        testQR = new QR(testStr);
    }


    @Test
    public void hashTest(){
        assertEquals(testQR.getHash(), DigestUtils.sha256Hex(testStr));
    }

    @Test
    public void scoreTest(){
        String testHash1 = "22222222222";
        String testHash2 = "000000";
        String testHash3 = "22222222220";
        String testHash4 = "12c4a67f90";
        String testHash5 = "fffff";
        String testHash6 = "abcddf";
        String testHash7 = "DA6B387601834350B994A8987A94793C555E91FAF9322C0D80025DD45821DBD5";

        assertEquals(testQR.calculateScore(testHash1), 1024);
        assertEquals(testQR.calculateScore(testHash2), 3200000);
        assertEquals(testQR.calculateScore(testHash3), 513);
        assertEquals(testQR.calculateScore(testHash4), 10);
        assertEquals(testQR.calculateScore(testHash5), 50625);
        assertEquals(testQR.calculateScore(testHash6), 17);
        assertEquals(testQR.calculateScore(testHash7), 122);

    }

}
