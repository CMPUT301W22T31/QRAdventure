package com.example.qradventure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;


/**
 * Activity where the logged in player can manage their account
 */
public class AccountActivity extends AppCompatActivity {
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account Activity");

        // get the account from the singleton
        account = CurrentAccount.getInstance().getCurrentAccount();

        // give info to textviews to display
        // TODO: Move these to onResume() in case of updated info.
        String username = account.getUsername();
        String email = account.getEmail();
        String phoneNumber = account.getPhoneNumber();
        TextView displayUsername = findViewById(R.id.user_username);
        displayUsername.setText(username);
        TextView displayEmail = findViewById(R.id.user_email);
        displayEmail.setText(email);
        TextView displayPhoneNumber = findViewById(R.id.user_phone_number);
        displayPhoneNumber.setText(phoneNumber);

        //

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
     * Sends to account activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToAccount(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }


    /**
     * Sends to search player activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToSearchPlayers(View view) {
        Intent intent = new Intent(this, SearchPlayersActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to leaderboard activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to scan activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScan(View view) {
//        Intent intent = new Intent(this, ScanActivity.class);
//        startActivity(intent);


        // TODO: Could activate camera immediately? Without need for button click?
        // button logic: activates camera on click
        //Button qrButton = findViewById(R.id.qr_button);

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
     * This method is called whenever a QR code is scanned
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
        Intent intent = new Intent(AccountActivity.this, PostScanActivity.class);
        intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
        startActivity(intent);
    }




}



