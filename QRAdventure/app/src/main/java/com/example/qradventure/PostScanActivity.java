package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Activity that comes immediately after scanning a QR code.
 * Allows the player to manage and interact with the code they have just scanned.
 */
public class PostScanActivity extends AppCompatActivity {
    private QR qr;
    private String recordID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan);
        setTitle("Post Scan Activity");

        // unfold intent, create QR object.
        Intent intent = getIntent();
        String QRContent = intent.getStringExtra(getString(R.string.EXTRA_QR_CONTENT));
        Log.d("logdebug", "intent received " + QRContent);
        qr = new QR(QRContent);

        // TODO: delete, temp for testing
        Log.d("logs", "intent received " + QRContent);

        // For testing purposes, display a dialog of the QR scanned
        new AlertDialog.Builder(PostScanActivity.this).setTitle("Result")
                .setMessage(QRContent)
                .setPositiveButton("QR code scanned", null)
                .setNegativeButton("Cancel", null)
                .create().show();

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

                // Add User to list of user scanned this qr
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("Username", myAccount.getUsername());
                docRef.collection("Scanned By").document(myAccount.getUsername()).set(userData);

                CollectionReference RecordDB = db.collection("RecordDB");

                QueryHandler addRecord = new QueryHandler();

                String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                addRecord.addRecord(androidDeviceID, qr, myAccount, recordID);

            }

            // ====== database logic concluded ======
            // send user to a different activity (which? Account for now?).
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            Log.d("logs", "Something went wrong");
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
        // related: need a setter method in QR.java?
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