package com.example.qradventure;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Class with just one method that we call whenever we want to launch the QR scanner
 *
 *      * Citation for using the Scanning library
 *      * Website:https://androidapps-development-blogs.medium.com
 *      * link:https://androidapps-development-blogs.medium.com/qr-code-scanner-using-zxing-library-in-android-fe667862feb7
 *      * authir: Golap Gunjun Barman, https://androidapps-development-blogs.medium.com/
 */
public class scanner {

    public static void scan(Activity activity){
        IntentIntegrator tempIntent = new IntentIntegrator(activity);
        tempIntent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        tempIntent.setCameraId(0);
        tempIntent.setOrientationLocked(false);
        tempIntent.setPrompt("");
        tempIntent.setBeepEnabled(true);
        tempIntent.setBarcodeImageEnabled(true);
        tempIntent.initiateScan();
    }



}
