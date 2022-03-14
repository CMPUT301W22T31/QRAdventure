package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that displays a global leaderboard.
 */
public class LeaderboardActivity extends AppCompatActivity {

    /**
     * TODO: Develop activity (part 4)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setTitle("Leaderboard");
    }

    /**
     * Sends to profile activity. Called when a player is clicked.
     * TODO: Part 4
     * @param view: unused (button)
     */
    public void goToProfile(View view) {
        // intent extra the username
    }
}