package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

/**
 * Startup Activity
 *
 */
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    BottomNavigationView navbar;



    /**
     * **TEMP** logs into a default test account
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Main Activity");

        // make sure to do this anytime an activity has a navbar
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);

        navbar.setOnItemSelectedListener((item) ->  {
            switch(item.getItemId()) {
                case R.id.my_account:
                    Intent intent1 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.leaderboards:
                    Intent intent2 = new Intent(getApplicationContext(), LeaderboardActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.scan:
                    // Use IntentIntegrator to activate camera
                    IntentIntegrator tempIntent = new IntentIntegrator(MainActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();
                    break;
                case R.id.search_players:
                    Intent intent4 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.map:
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
            }
            return true;

        });

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // DUMMY TEST ACCOUNT
        // TODO: DELETE
        Account account = new Account("Default Test Account", "temp", "temp", "temp", "temp");
        CurrentAccount.setAccount(account);

        //Get local android device ID
        String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        // ====== Query for accounts with matching device_id field ======
        // send to registration if no matching documents exist
        QueryHandler q = new QueryHandler();

        q.getLoginAccount(androidDeviceID, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {
                Intent intent;
                Boolean alreadyCreated = (Boolean)args.get(0);
                if (alreadyCreated){
                    intent = new Intent(MainActivity.this, AccountActivity.class);
                }
                else{
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }

                // disable backward navigation to this activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

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
        /**
         * TEMP: intent to MapActivity
         * Due to asynchronous query execution, reconstructing records is not done soon enough.
         * to "hide" the problem, temporarily send to MapActivity instead of AccountActivity.
         * This gives the query enough time to finish and display properly!
         */
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        // disable backward navigation to this activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    /**
     * ** TEMPORARY ** TODO: DELETE
     * Sends to login activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void TEMPgoToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}