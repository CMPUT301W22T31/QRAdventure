package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OwnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
    }

    /**
     * Sends to MyCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToCodes(View view) {
        Intent intent = new Intent(this, MyCodesActivity.class);
        intent.putExtra("Owner", "Owner");
        startActivity(intent);
    }

    /**
     * Sends to SearchPlayers activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToPlayers(View view) {
        Intent intent = new Intent(this, SearchPlayersActivity.class);
        intent.putExtra("Owner", "Owner");
        startActivity(intent);
    }
}