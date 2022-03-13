package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;




//TODO: This class is now redundant. Should be deleted eventually

/**
 * Activity that controls Camera activation
 * Leads to PostScanActivity after scanning a QR.
 */
public class ScanActivity extends AppCompatActivity{
    public QR scannedQR;
    public ArrayList<QR> globalQRData = new ArrayList<QR>(); // TODO: used in Test. Review this?


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // TODO: Could activate camera immediately? Without need for button click?
        // button logic: activates camera on click
        Button qrButton = findViewById(R.id.qr_button);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use IntentIntegrator to activate camera
                IntentIntegrator tempIntent = new IntentIntegrator(ScanActivity.this);
                tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                tempIntent.setCameraId(0);
                tempIntent.setOrientationLocked(false);
                tempIntent.setPrompt("Scanning");
                tempIntent.setBeepEnabled(true);
                tempIntent.setBarcodeImageEnabled(true);
                tempIntent.initiateScan();
            }
        });
    }

    /**
     * This method is called whenever a QR code is scanned
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity
        String content = result.getContents();
        goToPostScan(content);
    }


    /**
     * Called when a QR is successfully scanned
     * Sends to PostScanActivity, with the QR contents in the intent
     */
    public void goToPostScan(String QRContent) {
        Intent intent = new Intent(ScanActivity.this, PostScanActivity.class);
        intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), QRContent);
        startActivity(intent);
        //TODO: Can move this logic into onActivityResult? This method redundant.
    }


}

