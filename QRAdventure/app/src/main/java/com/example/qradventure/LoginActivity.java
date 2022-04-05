package com.example.qradventure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
        Button LoginQRButton = findViewById(R.id.buttonLogin2QR);

        LoginQRButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {

                                                 AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                 builder.setMessage("You will be logged out of all other devices and the login code will be made invalid")
                                                         .setTitle("Warning");

                                                 builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialogInterface, int i) {
                                                         scanner.scan(LoginActivity.this);
                                                     }
                                                 });
                                                 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialogInterface, int i) {
                                                                 return;
                                                             }
                                                         });
                                                 AlertDialog dialog = builder.create();
                                                 dialog.show();

                                             }
                                         });

                                //scanner.scan(LoginActivity.this);


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


                // validate data
                InputValidator inputValidator = new InputValidator();
                Boolean validUser = inputValidator.checkUser(username);
                Boolean validPhone = inputValidator.checkPhone(phoneNumber);
                Boolean validEmail = inputValidator.checkEmail(email);

                if (!validUser || !validPhone || !validEmail) {
                    if (!validUser) {
                        Context context = getApplicationContext();
                        CharSequence text = "Username must be 3-18 characters";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    if (!validPhone) {
                        Context context = getApplicationContext();
                        CharSequence text = "10 digits, no spaces";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    if (!validEmail) {
                        Context context = getApplicationContext();
                        CharSequence text = "Must provide valid email";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                else {  // all data has been provided in legal format
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity
        String content = result.getContents();

        Log.d("meme", "BOOM ");
        if (content != null && content.contains("QRLOGIN-")) {

            QueryHandler q = new QueryHandler();
            String deviceID = content.toString().split("-")[1];
            q.getLoginAccount(deviceID, new Callback() {
                Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
                @Override
                public void callback(ArrayList<Object> args) {
                    Account account =CurrentAccount.getAccount();
                    DocumentReference docRef = db.collection("AccountDB").document(account.getUsername());
                    docRef.update("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }
            });
        }



            Log.d("meme", "BOOM ");
            //Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
        }

    }


