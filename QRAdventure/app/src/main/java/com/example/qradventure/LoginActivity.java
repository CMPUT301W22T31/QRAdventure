package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * LoginActivity
 * Handles Account Registration
 * TODO: Rename Activity
 */
public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private Account currentAccount;

    /**
     * Sets up button listener for account registration
     * Contains logic for account registration
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();

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
                data.put("bestQR", 0);
                data.put("scanCount", 0);

                if (!username.matches("")) {

                    // Check for a document matching the input username
                    QueryHandler query = new QueryHandler();

                    query.checkNameTaken(data, username, new Callback() {
                        @Override
                        public void callback(ArrayList<Object> args) {

                            Boolean alreadyCreated = (Boolean)args.get(0);

                            if (!alreadyCreated){
                                Context context = getApplicationContext();
                                CharSequence text = "Username available! Creating account...";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                // Set data. Could add success/fail listeners?


                                // on success: proceed to app!
                                signedIn();
                            }else{
                                Context context = getApplicationContext();
                                CharSequence text = "Username is taken";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                // TODO: Does this case need extra logic? Or just a toast?
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