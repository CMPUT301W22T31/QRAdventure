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
    private String username;
    BottomNavigationView navbar;
    Account account = CurrentAccount.getAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // unpack intent to get account username
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.EXTRA_USERNAME));

        // query DB for username. Pull relevant fields to display
        // TESTING: use dummy data
        setTextViews(username, "March6", "March6", "3");

        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
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
                case R.id.map:
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
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

    /**
     * Updates the textviews to display user data
     * @param name - account username
     * @param email - account email
     * @param phone - account phone #
     * @param score - account score
     */
    public void setTextViews(String name, String email, String phone, String score) {
        // get reference to the textviews
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvTotalScore = findViewById(R.id.tvTotalScore);

        // set textview text
        tvUsername.setText(name);
        tvPhone.setText(email);
        tvEmail.setText(phone);
        tvTotalScore.setText(score);
    }
}