package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


/**
 * Activity that displays all the codes scanned by a player.
 * This activity is navigated from the players profile. Anyone can access this activity.
 * NOTE: This is functionally different from MyCodesActivity
 */
public class ViewCodesActivity extends AppCompatActivity {
    String username;
    ArrayList<Record> records;
    GridView qrList;
    FirebaseFirestore db;
    QRListAdapter qrListAdapter;
    BottomNavigationView navbar;
    String LOG = "VCActivity";

    /**
     * Initialize Gridview and click listener
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_codes);
        db = FirebaseFirestore.getInstance();

        // unpack intent to get account username
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.EXTRA_USERNAME));

        // enable GridView
        qrList = findViewById(R.id.qr_list);
        records = new ArrayList<Record>();
        qrListAdapter = new QRListAdapter(this, records);
        qrList.setAdapter(qrListAdapter);

        // on click listener logic
        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), QRPageActivity.class);

                Record clickedRecord = records.get(position);

                intent.putExtra("QRtitle", clickedRecord.getQRHash().substring(0,4));
                intent.putExtra("QRHash", clickedRecord.getQRHash());
                Bitmap image = clickedRecord.getImage();
                intent.putExtra("QRPicture", image);
                startActivity(intent);
            }
        });

        // query DB to get records
        loadRecords();


        // ====== NAVBAR ======
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        navbar.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.leaderboards:
                    Log.d("check", "WORKING???");
                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.search_players:
                    Log.d("check", "YES WORKING???");
                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.scan:
                    // Use IntentIntegrator to activate camera
                    scanner.scan(ViewCodesActivity.this);
                    break;
                case R.id.map:
                    Intent intent4 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.my_account:
                    Intent intent5 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        });

    }

    /**
     * Reconstructs the records associated with username
     */
    public void loadRecords() {
        // dummy account since Record requires an account
        QueryHandler query = new QueryHandler();

        query.loadRecords(username, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {

                for (Object o: args){
                    records.add((Record)o);

                }
                qrListAdapter.notifyDataSetChanged();

            }
        });

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
        Account account = CurrentAccount.getAccount();

        if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
            Intent intent = new Intent(ViewCodesActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);

        }else{
            String text = "You have already scanned that QR";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }



}