package com.example.qradventure;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

public class scanner {

    public static void scan(Activity activity){
        IntentIntegrator tempIntent = new IntentIntegrator(activity);
        tempIntent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        tempIntent.setCameraId(0);
        tempIntent.setOrientationLocked(false);
        tempIntent.setPrompt("Scanning");
        tempIntent.setBeepEnabled(true);
        tempIntent.setBarcodeImageEnabled(true);
        tempIntent.initiateScan();
    }



}
