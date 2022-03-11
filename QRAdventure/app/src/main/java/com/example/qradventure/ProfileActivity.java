package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Activity displaying the profile of any player. Anyone can access this activity.
 */
public class ProfileActivity extends AppCompatActivity {
    Account account = CurrentAccount.getAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // unpack intent to get account username
        // query DB for username. Pull relevant fields to display
        setTitle("USERNAME123456789s profile");
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