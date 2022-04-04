package com.example.qradventure.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;


//import com.example.qradventure.databinding.ActivityMainBinding;
import com.example.qradventure.model.Callback;
import com.example.qradventure.model.QueryHandler;
import com.example.qradventure.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Startup Activity
 * Checks for existing account and proceeds to app
 * Otherwise, if no account exists, sends to LoginActivity
 */
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    BottomNavigationView navbar;

    /**
     * Holds logic for sending app to register, or proceed to app
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        //Get local android device ID
        String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // ====== Query for accounts with matching device_id field ======
        // send to registration if no matching documents exist
        QueryHandler q = new QueryHandler();

        // Check if account for this device had been created
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

}