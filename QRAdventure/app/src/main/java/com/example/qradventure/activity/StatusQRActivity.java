package com.example.qradventure.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.qradventure.R;
import com.example.qradventure.model.Account;
import com.example.qradventure.model.CurrentAccount;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


/**
 * Activity where logged in user can access their Status QR Code
 */
public class StatusQRActivity extends AppCompatActivity {

    Account account;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_qractivity);
        setTitle("Access Game Status QR Code");

        imageView = (ImageView)findViewById(R.id.status_qr_image_view);

        //
        account = CurrentAccount.getAccount();
        String highestQR = Integer.toString(account.getHighestQR());
        String lowestQR = Integer.toString(account.getLowestQR());
        String username = account.getUsername();
        String totalScore = Integer.toString(account.getTotalScore());
        String totalScanned = Integer.toString(account.getTotalCodesScanned());

        String statsValues = "QRSTATS-" + username + '-' + totalScore + '-' + totalScanned + '-' + highestQR + '-' + lowestQR;
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(statsValues, BarcodeFormat.QR_CODE
                    , 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}