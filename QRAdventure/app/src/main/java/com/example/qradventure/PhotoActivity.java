package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 * Activity where player can take a photo related to the QR they just scanned. Uses the camera.
 */
public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("Take a photo");

    }
}