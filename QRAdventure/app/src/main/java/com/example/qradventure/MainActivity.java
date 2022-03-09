package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Startup Activity
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

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // create a dummy account
        Account account = new Account("Default Test Account", "temp", "temp", "temp", "temp");
        // set CurrentAccount to this dummy account
        CurrentAccount.setAccount(account);

        CollectionReference userRecords = db.collection("AccountDB");

        //Get local android device ID
        String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        final boolean[] recognizedDeviceID = {false};
        //Query for device ID
        Query accountsQuery = userRecords
                .whereEqualTo("device_id", androidDeviceID);  //Second argument should be device ID

        accountsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //device ID is recognize so fetch the associated account details
                    for(QueryDocumentSnapshot doc: task.getResult()) {


                        //Fetch the user account
                        Account fetchedAccount;
                        String email = (String) doc.getData().get("E-mail");
                        String loginQR = (String) doc.getData().get("LoginQR");
                        String phoneNumber = (String) doc.getData().get("Phone Number");
                        String statusQR = (String) doc.getData().get("StatusQR");
                        String username = (String) doc.getId();

                        fetchedAccount = new Account(username, email, phoneNumber, loginQR, statusQR);
                        CurrentAccount.setAccount(fetchedAccount);
                        recognizedDeviceID[0] = true;
                    }
                }
            }
        });

        if (!recognizedDeviceID[0]) {
            //Device ID not recognized, send user to create a new account screen
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("disable_back_button", true);
            startActivity(intent);

        }

        // DELETE THIS LATER - Huey
        // As of right now we are logged in as Default Test Account on start up so I want to get all their records once the app boots up
        // This is so that we have the records to be displayed in pages.

        Log.d("logs","testing query");

        // get all the added QR's by the user and put them in a list


        // The following is a query to grab all the QR codes that the player has added in their account
        // This is really long because this is two calls to the firebase db
        // Query steps:
        // 1.) I need to grab all the QR codes added by the user in the AccountDB collection
        // 2.) I need to grab all the scores by those QR codes in the QRDB
        // 3.) combine those two to create a new QR object and store it on the singleton account class

        // This probably should be put in a query handle class...
        try {
            db.collection("AccountDB").document(account.getUsername()).collection("My QR Records")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String qrHash = (String) document.getData().get("QR");
                                    db.collection("QRDB").document(qrHash).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String qrScoreValue = (String) document.getData().get("Score");
                                                DocumentSnapshot document = task.getResult();
                                                String qrScore = "" + document.getData().get("Score");
                                                int qrValue = Integer.parseInt(qrScore);
                                                QR qr = new QR(qrHash, qrValue, null, null);
                                                Log.d("logs", qrHash + " " + qrValue);
                                                Record newRecord = new Record(account, qr);
                                                account.addRecord(newRecord);
                                            } else {
                                                Log.d("logs", "Cached get failed: ", task.getException());
                                            }
                                        }
                                    });
                                }
                            } else {
                                Log.d("logs", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        // END OF QUERY
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
            }
            return false;
        });


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
     * Sends to scan activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
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
     * ** TEMPORARY **
     * Sends to login activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void TEMPgoToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}