package com.example.qradventure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;



import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main, landing activity of the app
 */
public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String username, email, phoneNumber, LoginQR, StatusQR;
    ArrayList<Record> myRecords;

    /**
     * Part of the standard activity lifecycle
     * TODO: Will this activity only be created once? Or each time is it navigated to?
     *       How we want to design our activity lifecycle/stack and navigation.
     *
     * @param savedInstanceState
     *       TODO
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");

        /******** Initialize player account data ********/
        // Dummy data for now
        username = "username3";
        email = "user3@email.com";
        phoneNumber = "+1 780-999-9999";
        LoginQR = "username3LoginQRHash";
        StatusQR = "username3StatusQRHash";

        // Create new user
        User newUser = new User(username, email, phoneNumber, LoginQR, StatusQR, myRecords);

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // Get a top level reference to the collection
        final CollectionReference AccountDB = db.collection("AccountDB");

        // Putting Player data into HashMap
        HashMap<String, Object> data = new HashMap<>();
        data.put("E-mail", email);
        data.put("Phone Number", phoneNumber);
        data.put("LoginQR", LoginQR);
        data.put("StatusQR", StatusQR);
        HashMap<String, Object> myScannedQR = new HashMap<>();
        data.put("My QR Records", myScannedQR);

        // Add data to the player document
        AccountDB.document(username)
                 .set(data);

        // Update Firestore database
        AccountDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
            }
        });
        /******** Initializing account done ********/

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
}