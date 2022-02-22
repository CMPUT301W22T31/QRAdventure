package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 * Activity where anyone can view or add comments attached to a particular QR code.
 */
public class CommentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setTitle("Player Comments");

        // unpack intent to get which QR comments to display

    }
}