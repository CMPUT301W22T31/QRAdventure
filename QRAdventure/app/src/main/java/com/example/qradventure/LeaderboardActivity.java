package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that displays a global leaderboard.
 * Can change which ranking criteria to sort by (Score, count, etc).
 */
public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setTitle("Leaderboard");


    }

    /**
     * Sends to profile activity. Called when a player is clicked.
     * TEMPORARY: Using a button to navigate. Will be changed to accommodate other navigation.
     * @param view: unused
     */
    public void goToProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        // use intent to carry over which profile was clicked?
        startActivity(intent);
    }
}