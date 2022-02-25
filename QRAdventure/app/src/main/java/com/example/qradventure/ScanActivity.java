package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity where user can scan a QR Code. Uses the phone camera.
 * After a successful scan, immediately sends to PostScanActivity to manage the scan
 */
public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle("Scan Activity");


    }

    /**
     * **PLACEHOLDER**
     * Temporarily used to access PostScanActivity
     */
    public void goToPostScan(View view) {
        Intent intent = new Intent(this, PostScanActivity.class);
        startActivity(intent);
    }
}