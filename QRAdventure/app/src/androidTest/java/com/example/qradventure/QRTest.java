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
        Log.i("hashTest", testQR.getHash());

        assertEquals(testQR.getHash(), DigestUtils.sha256Hex(testStr));
    }

}
