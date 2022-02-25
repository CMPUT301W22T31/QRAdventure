package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that displays all the codes scanned by a player.
 * This activity is reached from the players profile. Anyone can access this activity.
 * NOTE: This is functionally different from MyCodesActivity
 */
public class ViewCodesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_codes);
        setTitle("USERNAME123456789s Codes");

        // unpack the intent to get the player to display

    }

    /**
     * Sends to QRPage activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToQRPage(View view) {
        Intent intent = new Intent(this, QRPageActivity.class);
        startActivity(intent);
    }
}