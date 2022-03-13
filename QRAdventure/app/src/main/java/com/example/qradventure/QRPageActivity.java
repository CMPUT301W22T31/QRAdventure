package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


/**
 * Activity that shows details about a particular QR code.
 * Anyone can access. Further leads to activities ScannedBy and Comments
 */
public class QRPageActivity extends AppCompatActivity {
    String hash;
    String title;
    TextView QRTitle;
    String recordID;
    Account currentAccount = CurrentAccount.getAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpage);
        setTitle("QR-123456");
        QRTitle = findViewById(R.id.qr_title_header);
        Bundle bundle = getIntent().getExtras(); // get string from previous activity
        title = bundle.getString("QRtitle");
        hash = bundle.getString("QRHash");

        if (title != null) {
            QRTitle.setText(title);
        }

        // unpack Intent to get the hash (String)
        // query DB for that hash to get relevant fields

    }

    /**
     * Sends to ScannedBy activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        Intent intent = new Intent(this, ScannedByActivity.class);
        // add QR hash to the intent (?)
        startActivity(intent);
    }

    /**
     * Sends to Comments activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToComments(View view) {
        Intent intent = new Intent(this, CommentsActivity.class);
        recordID = currentAccount.getUsername() + "-" + hash;
        // add QR hash to intent (?)
        intent.putExtra("QR Hash", hash);
        startActivity(intent);
    }
}