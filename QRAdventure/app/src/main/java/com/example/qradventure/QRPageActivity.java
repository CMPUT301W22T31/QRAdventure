package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that shows details about a particular QR code.
 * Anyone can access. Further leads to activities ScannedBy and Comments
 */
public class QRPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpage);
        setTitle("QR-123456");


    }

    /**
     * Sends to ScannedBy activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        Intent intent = new Intent(this, ScannedByActivity.class);
        // add QR identifier to the intent (?)
        startActivity(intent);
    }

    /**
     * Sends to Comments activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToComments(View view) {
        Intent intent = new Intent(this, CommentsActivity.class);
        // add qr identifier to intent (?)
        startActivity(intent);
    }
}