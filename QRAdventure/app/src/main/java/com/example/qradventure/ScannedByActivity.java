package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 * Activity that displays a list of all the players that have scanned a particular QR code.
 */
public class ScannedByActivity extends AppCompatActivity {
    String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_by);
        setTitle("Scanned By:");

        // unpack intent to get QR code hash
        // query DB by QR hash to get relevant fields

    }
}