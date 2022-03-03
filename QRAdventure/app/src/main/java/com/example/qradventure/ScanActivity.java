package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;

/*** TODO: Add new QR to Firestore QRDB and RecordDB ***/

/**
 *This is the screen which shows up before
 */
public class ScanActivity extends AppCompatActivity{

    /**
     * The onCreate method for the camera.
     */
    public ArrayList<QR> globalQRData = new ArrayList<QR>();

    public QR scannedQR;
    public String scannedQRHash;
    public String recordID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle("Scan Activity");
      
        Button qrButton = findViewById(R.id.qr_button);

        Intent intent = getIntent();

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        //Note: should display a dialogue here
        new AlertDialog.Builder(ScanActivity.this).setTitle("Result")
                .setMessage(result.getContents()).setPositiveButton("QR code scanned", null)
                .setNegativeButton("Cancel", null)
                .create().show();

        scannedQR = new QR(result.getContents());

        // Update Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create New QR
        CollectionReference QRDB = db.collection("QRDB");

        HashMap<String, Object> QRData = new HashMap<>();
        QRData.put("Score", scannedQR.getScore(scannedQR.getHash()));
        HashMap<String, Object> locationData = new HashMap<>();
        QRData.put("Geolocation", locationData);
        HashMap<String, Object> scannedByData = new HashMap<>();
        scannedByData.put(MainActivity.currentUser.getUsername(), MainActivity.currentUser);
        QRData.put("Scanned By", scannedByData);

        QRDB.document(scannedQR.getHash()).set(data);
        QRDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
            }
        });

        // Create New Record
        CollectionReference RecordDB = db.collection("RecordDB");

        HashMap<String, Object> recordData = new HashMap<>();
        recordData.put("User", MainActivity.currentUser);
        recordData.put("QR", scannedQR);

        recordID = MainActivity.currentUser.getUsername() + "-" + scannedQR.getHash();

        RecordDB.document(recordID).set(recordData);
        RecordDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
            }
        });



    }


    /**
     * PLACEHOLDER
     * Temporarily used to access PostScanActivity
     *
     * @param view
     */

    public void goToPostScan(View view) {
        Intent intent = new Intent(this, PostScanActivity.class);
        startActivity(intent);
    }
}

