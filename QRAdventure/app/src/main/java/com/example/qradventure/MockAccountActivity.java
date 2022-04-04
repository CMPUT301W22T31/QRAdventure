package com.example.qradventure;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.example.qradventure.model.Account;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * class used to test the status and login QRs. Takes in a QR from the test method
 */
public class MockAccountActivity extends AccountActivity {

    String testQR;
    FirebaseFirestore db;
    Account old;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testQR = getIntent().getStringExtra("TEST");
        old = CurrentAccount.getAccount();
        this.onActivityResult(0, 0, new Intent());


    }

    /**
     * for resetting the account back to normal after testing the login QR
     */
    public void restoreStatus(){
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("AccountDB").document(old.getUsername());
        docRef.update("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    /**
     * manually sets the QR content
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        content = testQR;
        super.onActivityResult(requestCode, resultCode, data);
        this.restoreStatus();


    }
}
