package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity displaying the profile of any player. Anyone can access this activity.
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("USERNAME123456789s profile");

        // unpack the intent to get which player to display

    }

    /**
     * Sends to ViewCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToViewCodes(View view) {
        Intent intent = new Intent(this, ViewCodesActivity.class);
        startActivity(intent);
    }
}