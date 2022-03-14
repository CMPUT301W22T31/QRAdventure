package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * LoginActivity
 * Checks for an account associated with this device and signs them in
 * Otherwise, this activity handles account creation.
 */
public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private Account currentAccount;
    boolean disableBackButton;

    /**
     * Contains logic for existing account login
     * Sets up button listener for account registration
     * Contains logic for account registration
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();

        /* TODO: add logic to query db for an account that belongs to this device
        /* Alternative method: This device stores their account locally, so retrieve it!
        /* If exists, *must* set attribute: currentAccount =
        /*
        /* if currentAccount was found and set successfully, call: signedIn();
         */

        // ========== Account Registration ==========
        Button createButton = findViewById(R.id.buttonLogin);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get user input from textviews
                EditText username_entered = findViewById(R.id.editText_username);
                String username = username_entered.getText().toString();
                EditText phone_number_entered = findViewById(R.id.editText_phone_number);
                String phoneNumber = phone_number_entered.getText().toString();
                EditText email_entered = findViewById(R.id.editText_email);
                String email = email_entered.getText().toString();
                // TODO: develop LoginQR and StatusQR
                String LoginQR = "usernameLoginQRHash";
                String StatusQR = "usernameStatusQRHash";

                // Create new user
                currentAccount = new Account(username, email, phoneNumber, LoginQR, StatusQR);

                //Get Device ID
                String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                // Put Player data into HashMap
                HashMap<String, Object> data = new HashMap<>();
                data.put("E-mail", email);
                data.put("Phone Number", phoneNumber);
                data.put("LoginQR", LoginQR);
                data.put("StatusQR", StatusQR);
                data.put("TotalScore", 0);
                data.put("device_id", androidDeviceID);

                if (!username.matches("")) {
                    DocumentReference docRef = db.collection("AccountDB").document(username);

                    // Check for a document matching the input username
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            // task is a document query
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Document exists, so username is taken!
                                    Context context = getApplicationContext();
                                    CharSequence text = "Username is taken";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                    // TODO: Does this case need extra logic? Or just a toast?

                                } else {
                                    // Document does not exist, so username is available!
                                    Context context = getApplicationContext();
                                    CharSequence text = "Username available! Creating account...";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    // Set data. Could add success/fail listeners?
                                    docRef.set(data);

                                    // on success: proceed to app!
                                    signedIn();
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
                    // user input was empty, notify them via toast:
                    Context context = getApplicationContext();
                    CharSequence text = "Username Empty";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        Intent intent = getIntent();
         disableBackButton = intent.getBooleanExtra("disable_back_button", false);

    }

    //Restrict back button usage when forcing user to make an account

    /**
     * Behaviour when back button has been pressed
     */
    @Override
    public void onBackPressed() {
        if (disableBackButton) {
            ;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * After logging in, set CurrentAccount and go to AccountActivity
     */
    public void signedIn() {
        // Sets the Current Account singleton
        CurrentAccount.setAccount(currentAccount);

        Intent intent = new Intent(this, AccountActivity.class);
        // Disable backward navigation
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}