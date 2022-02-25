package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Activity that comes immediately after scanning a QR code.
 * Allows the player to manage and interact with the code they have just scanned.
 */
public class PostScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan);
        setTitle("Post Scan Activity");


    }

    /**
     * Handles the logic of confirming a QR code scan. Called when respective button is clicked.
     *          ((making a record, adding player to ScannedBy, adding to account, Database?...))
     * @param view: unused
     */
    public void AddQR(View view) {
        //

    }

    /**
     * Aborts the scan; does not add to account. Called when respective button is clicked.
     * @param view: unused
     */
    public void clickDismiss(View view) {
        // abort scan, do not save ?

    }

    /**
     * Records the geolocation of the scan. Called when respective button is clicked.
     * @param view: unused
     */
    public void addGeolocation(View view) {
        // Could handle the logic itself, or it could set a flag to record/not record the geoloc?


    }

    /**
     * Sends to photo activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToPhoto(View view) {
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to ScannedBy activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        Intent intent = new Intent(this, ScannedByActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to Comments activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToComments(View view) {
        Intent intent = new Intent(this, CommentsActivity.class);
        startActivity(intent);
    }




}