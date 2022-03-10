package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that displays all the codes scanned by a player.
 * This activity is navigated from the players profile. Anyone can access this activity.
 * NOTE: This is functionally different from MyCodesActivity
 */
public class ViewCodesActivity extends AppCompatActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_codes);

        // unpack intent to get account username
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.EXTRA_USERNAME));

        // query DB to get their records / scanned qr codes (?)

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