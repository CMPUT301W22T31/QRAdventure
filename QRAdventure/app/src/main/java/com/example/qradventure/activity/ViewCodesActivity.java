package com.example.qradventure.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qradventure.model.Callback;
import com.example.qradventure.model.CurrentAccount;
import com.example.qradventure.utility.LocationGrabber;
import com.example.qradventure.utility.QRListAdapter;
import com.example.qradventure.model.QueryHandler;
import com.example.qradventure.R;
import com.example.qradventure.model.Account;
import com.example.qradventure.model.QR;
import com.example.qradventure.model.Record;
import com.example.qradventure.model.scanner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    FusedLocationProviderClient fusedLocationProviderClient;
    Account account;
    String content = null;

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
                Intent intent = getIntent();
                Intent QRPageIntent = new Intent(getApplicationContext(), QRPageActivity.class);

                String ownerRes = intent.getStringExtra("Owner");
                if (ownerRes != null) {
                    QRPageIntent.putExtra("Owner", "Owner");
                }

                Record clickedRecord = records.get(position);

                QRPageIntent.putExtra("QRtitle", clickedRecord.getQRHash().substring(0,4));
                QRPageIntent.putExtra("QRHash", clickedRecord.getQRHash());
                Bitmap image = clickedRecord.getImage();
                QRPageIntent.putExtra("QRPicture", image);
                startActivity(QRPageIntent);
            }
        });

        // query DB to get records
        loadRecords();

        // display header

        TextView header = findViewById(R.id.codes_header);
        header.setText(username+ "'s codes");
        header.setTextSize(35);
        if (username.length() >= 10) {
            header.setTextSize(25);
        }
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
    /**
     * This method is called whenever a QR code is scanned. Takes the user to PostScanActivity
     * This method is copied into every activity which we can clock the scannable button from
     *
     *
     * Citation for using the Scanning library
     * Website:https://androidapps-development-blogs.medium.com
     * link:https://androidapps-development-blogs.medium.com/qr-code-scanner-using-zxing-library-in-android-fe667862feb7
     * authir: Golap Gunjun Barman, https://androidapps-development-blogs.medium.com/
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (content == null){
            super.onActivityResult(requestCode, resultCode, data);
        }
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity

        if (content == null)
            content = result.getContents();

        if (account == null)
            account = CurrentAccount.getAccount();

        if (content.contains("QRSTATS-")) {
            Intent intent = new Intent(ViewCodesActivity.this, StatsActivity.class);

            // extract the username from QR content, and add it to intentExtra
            String username = content.split("-")[1];
            intent.putExtra(getString(R.string.EXTRA_USERNAME), username);

            // start activity
            startActivity(intent);

        }
        else if (content.contains("QRLOGIN-")) {
            QueryHandler q = new QueryHandler();
            String deviceID = content.toString().split("-")[1];
            q.getLoginAccount(deviceID, new Callback() {
                Intent intent = new Intent(ViewCodesActivity.this, AccountActivity.class);
                @Override
                public void callback(ArrayList<Object> args) {
                    DocumentReference docRef = db.collection("AccountDB").document(account.getUsername());
                    docRef.update("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }
            });
        }

        else if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
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