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

/**
 *This is the screen which shows up before
 */
public class ScanActivity extends AppCompatActivity{

    /**
     * The onCreate method for the camera.
     */

    public ArrayList<QR> globalQRData = new ArrayList<QR>(); // used in Test. Review?

    public QR scannedQR;
    public String scannedQRHash;
    public String recordID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle("Scan Activity");

        // button logic: activates camera
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
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity
        String content = result.getContents();
        goToPostScan2(content);
    }


    /**
     * PLACEHOLDER
     * Temporarily used to access PostScanActivity via button.
     *
     * @param view
     */

    public void goToPostScan(View view) {
        //Intent intent = new Intent(ScanActivity.this, PostScanActivity.class);
        //startActivity(intent);
    }

    /**
     * Called when a QR is successfully scanned
     * Sends to PostScanActivity, with the QR contents in the intent
     */
    public void goToPostScan2(String QRContent) {
        Intent intent = new Intent(ScanActivity.this, PostScanActivity.class);
        intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), QRContent);
        startActivity(intent);
    }

}

