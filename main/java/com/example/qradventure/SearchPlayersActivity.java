package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that lets anyone search for other players by their username
 */
public class SearchPlayersActivity extends AppCompatActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_players);
        setTitle("Search Players by Username");


    }

    /**
     * **PLACEHOLDER**
     * Temporary solution to access the Profile activity.
     * Button that sends you to Profile Activity
     */
    public void goToProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        // put username string into intent
        startActivity(intent);
    }

}