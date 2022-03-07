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
    BottomNavigationView navbar;
    Account account = CurrentAccount.getAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);

        // unpack intent to get account username
        // query DB for username. Pull relevant fields to display
        setTitle("USERNAME123456789s profile");

        navbar.setOnItemSelectedListener((item) ->  {
            switch(item.getItemId()) {
                case R.id.leaderboards:
                    Log.d("check", "WORKING???");
                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.search_players:
                    Log.d("check", "YES WORKING???");
                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.scan:
                    Intent intent3 = new Intent(getApplicationContext(), ScanActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
            }
            return false;
        });

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