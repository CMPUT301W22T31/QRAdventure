package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Activity that comes immediately after scanning a QR code.
 * Allows the player to manage and interact with the code they have just scanned.
 */
public class PostScanActivity extends AppCompatActivity {
    private int locationCount = 0;
    private QR qr;
    private String recordID;
    Account account;

    private final static int MY_REQUEST_CODE = 1;
    ActivityResultLauncher<Intent> getGeo;
    FusedLocationProviderClient fusedLocationProviderClient;
    /**
     * Sets layout and gets QR Content from intent
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_post_scan);
        setTitle("Post Scan Activity");
        // Get the account from the singleton
        account = CurrentAccount.getAccount();
        // unfold intent, create QR object.
        Intent intent = getIntent();
        String QRContent = intent.getStringExtra(getString(R.string.EXTRA_QR_CONTENT));
        qr = new QR(QRContent);

        // For testing purposes, display a dialog of the QR scanned
        new AlertDialog.Builder(PostScanActivity.this).setTitle("Result")
                .setMessage(QRContent)
                .setPositiveButton("QR code scanned", null)
                .setNegativeButton("Cancel", null)
                .create().show();

        // Grabs geolocation from adding geolocation activity
        getGeo = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Log.d("logs", "long: " + data.getStringExtra("longitude"));
                            Log.d("logs", "lat: " + data.getStringExtra("latitude"));
                            ArrayList<String> userGeo = new ArrayList<String>();
                            userGeo.add(data.getStringExtra("longitude"));
                            userGeo.add(data.getStringExtra("latitude"));
                            qr.setGeolocation(userGeo);
                        }
                    }
                });

    }

    /**
     * Called when respective button is clicked.
     * Handles the logic of completing a QR code scan;
     * Updates firestore with the QR and creates a record.
     * @param view: unused
     */
    public void AddQR(View view) {
        try {
            Account myAccount = CurrentAccount.getAccount();

            // get firestore collection and desired document
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("QRDB").document(qr.getHash());

            // reference: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
            // Check for a document matching the qr hash
            QueryHandler query = new QueryHandler();

            query.addQR(qr, new Callback() {
                @Override
                public void callback(ArrayList<Object> args) {
                    Boolean exists = (Boolean)args.get(0);

                    if (exists){
                            // Document exists, therefore QR already in DB!
                            Context context = getApplicationContext();
                            CharSequence text = "That QR is in the DB!";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            // TODO: append CURRENT ACCOUNT to the list of players that have scanned this QR
                            //       use set(data, SetOptions.merge());


                    }else{
                            // Document does not exist, therefore this QR is brand new!
                            Context context = getApplicationContext();
                            CharSequence text = "New QR! Adding to Database...";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                    }

                }
            });


            //====== Create New Record ======//

            recordID = myAccount.getUsername() + "-" + qr.getHash();

            // Add the record to the current account
            Account currentAccount = CurrentAccount.getAccount();
            Record toAdd = new Record(currentAccount, qr);

            if (!currentAccount.containsRecord(toAdd)) {

                currentAccount.addRecord(new Record(currentAccount, qr));

                CurrentAccount.setAccount(currentAccount);

                // Add User to list of user scanned this qr
                HashMap<String, Object> userData = new HashMap<>();

                if (qr.getGeolocation().size() != 0) {
                    Log.d("logs", "went in");
                    HashMap<String, Object> userLocation = new HashMap<>();

                    docRef.collection("Locations")
                            .orderBy("Index")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException error) {

                                    // update locationcount
                                    locationCount = queryDocumentSnapshots.size();

                                }
                            });

                    // Add details of geolocation
                    userLocation.put("Username", myAccount.getUsername());
                    userLocation.put("Longitude", qr.getGeolocation().get(0)); // first index is longitude
                    userLocation.put("Latitude", qr.getGeolocation().get(1));  // second index is latitude
                    userLocation.put("Index", locationCount);
                    docRef.collection("Locations").document(Integer.toString(locationCount)).set(userLocation);
                }

                docRef.collection("Scanned By").document(myAccount.getUsername()).set(userData);

                CollectionReference RecordDB = db.collection("RecordDB");

                QueryHandler addRecord = new QueryHandler();

                String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                addRecord.addRecord(androidDeviceID, qr, myAccount, recordID);

            }

            // ====== database logic concluded ======
            // send user to a different activity (which? Account for now?).
            // Huey - I changed the class to be account activity rather than main activity.
            //        Switching to main activity resets the account object, including location. I need location to be the same
            //        during the entire run of the program
            Intent intent = new Intent(this, AccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            Log.d("logs", qr.getGeolocation().toString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("logs", "Checking");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_REQUEST_CODE) {
                if (data != null) {
                    Log.d("logs", "Retrieved: ");
                    Log.d("logs", "Latitude " + data.getStringExtra("latitude"));
                    Log.d("logs", "Longitude " + data.getStringExtra("longitude"));

                }
            }
        }
    }

    /**
     * Aborts the scan; does not add to account. Called when respective button is clicked.
     * @param view: unused
     */
    public void clickDismiss(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    /**
     * Records the geolocation of the scan. Called when respective button is clicked.
     * @param view: unused
     */
    public void addGeolocation(View view) {

        if (ActivityCompat.checkSelfPermission(PostScanActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // grab location of user before map activity starts
            try {
                Intent intent = new Intent(this, AddGeolocationActivity.class);
                getGeo.launch(intent);
            }
            catch (Exception e){
                Log.d("logs", e.toString());
            }

        }
        else {
            ActivityCompat.requestPermissions(PostScanActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

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
            MapGrabber mapGrabber = new MapGrabber(fusedLocationProviderClient);
            mapGrabber.getLocation(this);
            Intent intent = new Intent(this, AddGeolocationActivity.class);
            getGeo.launch(intent);
            Log.d("logs", "Location after: " + account.getLocation().toString() );
        }

    }

    /**
     * Sends to photo activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToPhoto(View view) {
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to ScannedBy activity. Called when respective button is clicked.
     * Uses a queryhandler to retrieve the needed information and a callback to
     * go to the desired activity
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        QueryHandler q = new QueryHandler();

        q.getOthersScanned(qr.getHash(),new QueryCallback() {
            @Override
            public void callback(ArrayList<String> nameData, ArrayList<Long> scoreData) {

                Intent intent = new Intent(PostScanActivity.this, ScannedByActivity.class);

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

        intent.putExtra("QR Hash", qr.getHash());

        startActivity(intent);
    }

}