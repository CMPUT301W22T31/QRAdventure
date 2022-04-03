package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpage);

        db = FirebaseFirestore.getInstance();

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

        Button deleteButton = findViewById(R.id.button_delete_qr);
        deleteButton.setVisibility(View.INVISIBLE);

        // if (intent.getStringExtra("Owner") == "Owner) {
        //      deleteButton.setVisibility(View.VISIBLE);
        // }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQR(hash);
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


    /**
     * This method is called whenever a QR code is scanned. Takes the user to PostScanActivity
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
        if (content != null) {
            Intent intent = new Intent(QRPageActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        }
    }

    public void deleteQR(String hash) {
        db.collection("QRDB").document(hash)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Context context = getApplicationContext();
                        CharSequence text = "QR successfully deleted";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Context context = getApplicationContext();
                        CharSequence text = "Error deleting QR";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();                    }
                });
    }

}