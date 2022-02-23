package com.example.qradventure;

import android.util.Log;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class QR {

    private String hash;


    public QR(String QR){

        this.hash = DigestUtils.sha256Hex(QR);

    }

    public String getHash(){
        return this.hash;
    }


}
