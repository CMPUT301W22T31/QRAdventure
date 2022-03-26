package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Activity where the logged in player can manage their account
 * Displays number of codes scanned, total score, highest and lowest score
 * Leads to account's codes, stats, unique login code and status code
 */
public class AccountActivity extends AppCompatActivity {
    Account account;
    BottomNavigationView navbar;
    FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Sets layout and Enables navbar
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the account from the singleton
        account = CurrentAccount.getAccount();
        Log.d("logs", "Current location: " + account.getLocation().toString());

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

                    // Use IntentIntegrator to activate camera
                    IntentIntegrator tempIntent = new IntentIntegrator(AccountActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();

                    break;
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(AccountActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // grab location of user before map activity starts
                        try {

                            MapGrabber mapGrabber = new MapGrabber(fusedLocationProviderClient);
                            mapGrabber.getLocation(this);
                            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent5);
                        }
                        catch (Exception e){
                            Log.d("logs", e.toString());
                        }

                    }
                    else {
                            ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        }
                    break;
                case R.id.my_account:
                    // already on this activity. Do nothing.
                    break;
            }
            return false;
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        if (requestCode == 44) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.d("logs", "Grabbing location ");
            Log.d("logs", "Location before: " + account.getLocation().toString() );
            MapGrabber mapGrabber = new MapGrabber(fusedLocationProviderClient);
            mapGrabber.getLocation(this);
            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent5);
            Log.d("logs", "Location after: " + account.getLocation().toString() );
        }

    }
    /**
     * On resume, display all the textviews.
     * So if text data changes after returning TO this activity, the views are updated.
     */
    @Override
    protected void onResume() {
        super.onResume();

        account = CurrentAccount.getAccount();


        // Give info to textviews to display
        try {
            // get textviews
            TextView displayTotalScore = findViewById(R.id.total_score);
            TextView displayCodesScanned = findViewById(R.id.codes_scanned);
            TextView displayLowestQR = findViewById(R.id.lowest_qr);
            TextView displayHighestQR = findViewById(R.id.highest_qr);
            TextView displayUsername = findViewById(R.id.user_username);
            TextView displayEmail = findViewById(R.id.user_email);
            TextView displayPhoneNumber = findViewById(R.id.user_phone_number);

            // set textviews
            String username = account.getUsername();
            String email = account.getEmail();
            String phoneNumber = account.getPhoneNumber();
            displayUsername.setText(username);
            displayEmail.setText(email);
            displayPhoneNumber.setText(phoneNumber);

            displayTotalScore.setText(detailFormatter(account.getTotalScore()));
            displayCodesScanned.setText(detailFormatter(account.getTotalCodesScanned()));
            displayLowestQR.setText(detailFormatter(account.getLowestQR()));
            displayHighestQR.setText(detailFormatter(account.getHighestQR()));

        }
        catch (Exception e) {
            Log.d("logs", "Something went wrong while displaying!!! ");
        }
    }

    /**
     * Changes the format of the number for readability
     * @param number
     *      The number to change the format of
     * @return the changed value as a String
     */
    private String detailFormatter(Number number) {
        // if the number is too big, put it in this format
        // for example, if the user has 1345 qr's scanned
        // it would say 1.34k in the page
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.00").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    /**
     * Sends to edit info activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToEditInfo(View view) {
        Intent intent = new Intent(this, EditInfoActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to StatusQR activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToStatusQR(View view) {
        Intent intent = new Intent(this, StatusQRActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to LoginQR activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToLoginQR(View view) {
        Intent intent = new Intent(this, LoginQRActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to MyStats activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToMyStats(View view) {
        Intent intent = new Intent(this, MyStatsActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to MyCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToMyCodes(View view) {
        Intent intent = new Intent(this, MyCodesActivity.class);
        startActivity(intent);
    }

    /**
     * Displays the camera for scanning a QR code.
     */
    public void goToScan() {

        // Use IntentIntegrator to activate camera
        IntentIntegrator tempIntent = new IntentIntegrator(AccountActivity.this);
        tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        tempIntent.setCameraId(0);
        tempIntent.setOrientationLocked(false);
        tempIntent.setPrompt("Scanning");
        tempIntent.setBeepEnabled(true);
        tempIntent.setBarcodeImageEnabled(true);
        tempIntent.initiateScan();
    }


    /**
     * This method is called whenever a QR code is scanned. Takes the user to PostScanActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // get the QR contents, and send it to next activity
            String content = result.getContents();
            if(content != null) {
                Intent intent = new Intent(AccountActivity.this, PostScanActivity.class);
                intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
                startActivity(intent);
        }
    }
}



