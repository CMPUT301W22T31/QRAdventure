package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

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
    FusedLocationProviderClient fusedLocationProviderClient;
    Account account;

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

        // Get the account from the singleton
        account = CurrentAccount.getAccount();

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                intent.putExtra("QRtitle", records.get(position).getQRHash().substring(0,4));
                intent.putExtra("QRHash", records.get(position).getQRHash());
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
                    IntentIntegrator tempIntent = new IntentIntegrator(ViewCodesActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();
                    break;
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(ViewCodesActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // grab location of user before map activity starts
                        try {
                            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
                            locationGrabber.getLocation(this);
                            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent5);
                        }
                        catch (Exception e){
                            Log.d("logs", e.toString());
                        }
                    }
                    else {
                        ActivityCompat.requestPermissions(ViewCodesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }
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
     * Grabs location of user before entering maps activity
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        if (requestCode == 44) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.d("logs", "Grabbing location ");
            Log.d("logs", "Location before: " + account.getLocation().toString() );
            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
            locationGrabber.getLocation(this);
            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent5);
            Log.d("logs", "Location after: " + account.getLocation().toString() );
        }
    }
}