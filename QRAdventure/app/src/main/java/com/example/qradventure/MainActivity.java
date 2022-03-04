package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();



        /* =========================================================================================
         * TODO: This should all be moved to some activity relating to account registration
         *       Later we will have to decide the logic about registration and which activity
         *       is the startup activity.
         *       Idea: LoginActivity is the startup activity, and it checks the device if it is
         *             associated with an account, or allows user to register if not.
         *       Keep this code temporarily as a means to test things?
         *       Probably delete all this before merging
         */

        Button createButton = findViewById(R.id.button_create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText username_entered = findViewById(R.id.editText_username);
                String username = username_entered.getText().toString();
                EditText phone_number_entered = findViewById(R.id.editText_phone_number);
                String phoneNumber = phone_number_entered.getText().toString();
                EditText email_entered = findViewById(R.id.editText_email);
                String email = email_entered.getText().toString();
                // Dummy data for now
                String LoginQR = "usernameLoginQRHash";
                String StatusQR = "usernameStatusQRHash";

                // Create new user
                currentUser = new Account(username, email, phoneNumber, LoginQR, StatusQR, null);

                // Putting Player data into HashMap
                HashMap<String, Object> data = new HashMap<>();
                data.put("E-mail", email);
                data.put("Phone Number", phoneNumber);
                data.put("LoginQR", LoginQR);
                data.put("StatusQR", StatusQR);

                CollectionReference AccountDB = db.collection("AccountDB");

                // reference: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
                // if input was non-empty
                if (!username.matches("")) {
                    DocumentReference docRef = db.collection("AccountDB").document(username);

                    // Check for a document matching the input username
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            // task is a query for a document matching the String username
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Document exists, therefore username is taken!
                                    Context context = getApplicationContext();
                                    CharSequence text = "Username already exists!";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                } else {
                                    // Document does not exist, therefore username is available!
                                    Context context = getApplicationContext();
                                    CharSequence text = "Username available! Creating account...";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    // TODO: add logic to add this account to firestore

                                }

                            } else {
                                // document query was not successful
                                Context context = getApplicationContext();
                                CharSequence text = "ERROR: query failed!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    // user input was empty; optionally add logic for this case?
                }
            }
        });
        // ====== outside button click logic ======
        //==========================================================================================

        //

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