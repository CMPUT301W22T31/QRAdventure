package com.example.qradventure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;


/**
 * Activity where the logged in player can manage their account
 */
public class AccountActivity extends AppCompatActivity {
    Account account;
    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account Activity");

        // get the account from the singleton
        account = CurrentAccount.getAccount();

        // give info to textviews to display
        // TODO: Move these to onResume() in case of updated info.

        try {
            // get textviews
            TextView displayTotalScore = findViewById(R.id.total_score);
            TextView displayCodesScanned = findViewById(R.id.codes_scanned);
            TextView displayLowestQR = findViewById(R.id.lowest_qr);
            TextView displayHighestQR = findViewById(R.id.highest_qr);

            // set textviews
            displayTotalScore.setText(detailFormatter(getTotalScore()));
            displayCodesScanned.setText(detailFormatter(getCodesScanned()));
            displayLowestQR.setText(detailFormatter(getLowestQR()));
            displayHighestQR.setText(detailFormatter(getHighestQR()));
        }
        catch (Exception e) {
            Log.d("logs", "Something went wrong while displaying!!! ");
        }

        String username = account.getUsername();
        String email = account.getEmail();
        String phoneNumber = account.getPhoneNumber();
        TextView displayUsername = findViewById(R.id.user_username);
        displayUsername.setText(username);
        TextView displayEmail = findViewById(R.id.user_email);
        displayEmail.setText(email);
        TextView displayPhoneNumber = findViewById(R.id.user_phone_number);
        displayPhoneNumber.setText(phoneNumber);

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
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
                    break;
                case R.id.my_account:
                    // already on this activity. Do nothing.
                    break;
            }
            return false;
        });

    }
    // if the number is too big, put it in this format
    // for example, if the user has 1345 qr's scanned
    // it would say 1.34k in the page
    private String detailFormatter(Number number) {
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
    // gets the lowest QR the player has scan to display
    private int getLowestQR() {
        // TODO: verify this works. Sometimes records are not present.
        int smallest = account.getMyRecords().get(0).getQRscore();
        for (Record record: account.getMyRecords()
             ) {
            if (record.getQRscore() < smallest){
                smallest = record.getQRscore();
            }
        }
        return smallest;
    }

    // gets the highest QR the player has scan to display
    private int getHighestQR() {
        int biggest = account.getMyRecords().get(0).getQRscore();
        for (Record record: account.getMyRecords()
        ) {
            if (record.getQRscore() > biggest){
                biggest = record.getQRscore();
            }
        }
        return biggest;
    }

    // gets the number of codes scanned by player so we can display it
    private int getCodesScanned() {
        return account.getMyRecords().size();
    }

    // gets the cumulative score player has scanned so we can display it
    private int getTotalScore() {
        int sum = 0;
        for (Record record: account.getMyRecords()) {
            sum += record.getQRscore();

        }
        return sum;
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
        try {
            String content = result.getContents();
            Log.d("logs", result.getContents());
            Intent intent = new Intent(AccountActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        }
        catch (Exception e) {
            Log.d("logs", e.toString());
        }
    }
}



