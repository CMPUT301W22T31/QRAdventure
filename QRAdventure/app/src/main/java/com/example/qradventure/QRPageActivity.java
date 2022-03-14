package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;


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

        // set textview for qr name
        // try: temporary to prevent crashes
        QRTitle = findViewById(R.id.qr_title_header);
        try {
            Bundle bundle = getIntent().getExtras(); // get string from previous activity
            title = bundle.getString("QRtitle");
            QRTitle.setText(title);
            hash = bundle.getString("QRHash");
        } catch(Exception e) {
            QRTitle.setText("PLACEHOLDER");
        }

        // ====== back button logic ======
        FloatingActionButton backButton = findViewById(R.id.button_back_to_MyCodes);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // enable navbar functionality
        BottomNavigationView navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        navbar.setOnItemSelectedListener((item) ->  {
            switch(item.getItemId()) {
                case R.id.leaderboards:
                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.search_players:
                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.scan:
                    IntentIntegrator tempIntent = new IntentIntegrator(QRPageActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.map:
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        });

    }

    /**
     * Sends to ScannedBy activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        QueryHandler q = new QueryHandler();

        q.getOthersScanned(hash,new QueryCallback() {
            @Override
            public void callback(ArrayList<String> nameData, ArrayList<Long> scoreData) {

                Intent intent = new Intent(QRPageActivity.this, ScannedByActivity.class);

                intent.putExtra("NAMES", nameData);
                intent.putExtra("SCORES", scoreData);

                startActivity(intent);
            }
        });
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