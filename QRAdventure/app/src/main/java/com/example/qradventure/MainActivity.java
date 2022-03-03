package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The main, landing activity of the app
 */
public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String username, email, phoneNumber, LoginQR, StatusQR;
    ArrayList<Record> myRecords;

    public static Account currentUser;

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

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // Get a top level reference to the collection
        // DocumentReference usernameDR = db.collection("AccountDB").document();

        Button createButton = findViewById(R.id.button_create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText username_entered = findViewById(R.id.editText_username);
                username = username_entered.getText().toString();
                EditText phone_number_entered = findViewById(R.id.editText_phone_number);
                phoneNumber = phone_number_entered.getText().toString();
                EditText email_entered = findViewById(R.id.editText_email);
                email = email_entered.getText().toString();
                // Dummy data for now
                LoginQR = "usernameLoginQRHash";
                StatusQR = "usernameStatusQRHash";

                // Create new user
                currentUser = new Account(username, email, phoneNumber, LoginQR, StatusQR, myRecords);

                // Putting Player data into HashMap
                HashMap<String, Object> data = new HashMap<>();
                data.put("E-mail", email);
                data.put("Phone Number", phoneNumber);
                data.put("LoginQR", LoginQR);
                data.put("StatusQR", StatusQR);
                HashMap<String, Object> myScannedQR = new HashMap<>();
                data.put("My QR Records", myScannedQR);

                // Add data to the player document
                CollectionReference AccountDB = db.collection("AccountDB");
                // AccountDB.document(username).set(data);
                 AccountDB.document(username)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("Username", username);
                intent.putExtra("E-mail", email);
                intent.putExtra("Phone Number", phoneNumber);
                intent.putExtra("LoginQR", LoginQR);
                intent.putExtra("StatusQR", StatusQR);
                startActivity(intent);

                // Update Firestore database
                AccountDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                    }
                });
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