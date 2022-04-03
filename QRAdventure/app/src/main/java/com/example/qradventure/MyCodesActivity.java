package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Activity where logged in user can access and manage the codes they have scanned
 * NOTE: This is functionally different from ViewCodesActivity.
 */
public class MyCodesActivity extends AppCompatActivity {
    GridView qrList;
    FirebaseFirestore db;
    BottomNavigationView navbar;
    FusedLocationProviderClient fusedLocationProviderClient;
    Account myAccount;

    /**
     * Sets button on click listeners
     * Holds logic to delete QR codes
     * Enables navbar
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_codes);

        // get db, account references
        db = FirebaseFirestore.getInstance();
        myAccount = CurrentAccount.getAccount();
        ArrayList<Record> accountRecords = myAccount.getMyRecords();

        // initialize adapter
        qrList = findViewById(R.id.qr_list);
        QRListAdapter qrListAdapter = new QRListAdapter(this, accountRecords);
        qrList.setAdapter(qrListAdapter);

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // ====== on click listener : intent to activity ======
        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent to QRPageActivity
                Intent intent = new Intent(getApplicationContext(), QRPageActivity.class);

                Record clickedRecord = accountRecords.get(position);
                intent.putExtra("QRtitle", clickedRecord.getQRHash().substring(0,4));
                intent.putExtra("QRHash", clickedRecord.getQRHash());
                Bitmap image = clickedRecord.getImage();
                intent.putExtra("QRPicture", image);

                startActivity(intent);
            }
        });


        // ====== Long Click Listener for Delete Functionality ======
        qrList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                new AlertDialog.Builder(MyCodesActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Do you want to delete this QR?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String QRRecord = myAccount.getUsername() + "-" + myAccount.getMyRecords().get(position).getQRHash();

                                Log.d("logs", QRRecord);

                                Record toDelete = accountRecords.get(position);

                                myAccount.removeRecord(toDelete.getQRHash());

                                QueryHandler delete = new QueryHandler();
                                try {
                                    delete.deleteRecord(myAccount, toDelete);

                                } catch (Exception e) {
                                    Log.d("logs", e.toString());
                                }

                                HashMap<String, Object> newScore = new HashMap<String, Object>();
                                newScore.put("TotalScore", myAccount.getTotalScore());

                                db.collection("AccountDB").document(myAccount.getUsername())
                                        .update(newScore);


                                CurrentAccount.setAccount(myAccount);
                                qrListAdapter.notifyDataSetChanged();

                            }

                        }).setNegativeButton("No",null)
                .show();

                return true;
            }
        });

        // ====== Navbar functionality ======
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
                    scanner.scan(MyCodesActivity.this);
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(MyCodesActivity.this,
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
                        ActivityCompat.requestPermissions(MyCodesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }
                    break;
            }
            return false;
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
            Log.d("logs", "Location before: " + myAccount.getLocation().toString() );
            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
            locationGrabber.getLocation(this);
            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent5);
            Log.d("logs", "Location after: " + myAccount.getLocation().toString() );
        }
    }


    /**
     * Activity is called when the camera scans a QR code. Processes the result and redirects to
     * PostScanActivity
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
            Intent intent = new Intent(MyCodesActivity.this, PostScanActivity.class);
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
