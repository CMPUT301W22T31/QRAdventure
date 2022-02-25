package com.example.qradventure;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;



/**
 *This is the screen which shows up before
 */
public class ScanActivity extends AppCompatActivity{


    // Both these variables are meant for testing. Later should try and find a better way to test


    /**
     * The onCreate method for the camera.
     */
    public ArrayList<QR> globalQRData = new ArrayList<QR>();
    public QR lastQR;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera);
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

        lastQR = new QR(result.getContents());
        globalQRData.add(lastQR);

    }

}
