package com.example.qradventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.example.qradventure.model.QR;

/**
 * For testing adding pictures
 */
public class MockPostScan extends PostScanActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        image = bitmapFromVector(getApplicationContext(), R.drawable.qr_code3); // random picture
        keepImage = true;
        qr = new QR("testQR");

        // View is set to somthing random because we don't use it
        this.AddQR(findViewById(R.id.buttonGoToScannedBy));
    }


    // This is for setting up the custom icon for the marker
    private Bitmap bitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth()
                ,vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);


        return bitmap;
    }
}
