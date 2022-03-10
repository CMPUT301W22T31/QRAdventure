package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.QuerySnapshot;

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
        qr = new QR(QRContent);

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
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    // task is a document query
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document exists, therefore QR already in DB!
                            Context context = getApplicationContext();
                            CharSequence text = "That QR is in the DB!";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            // TODO: append CURRENT ACCOUNT to the list of players that have scanned this QR
                            //       use set(data, SetOptions.merge());


                        } else {
                            // Document does not exist, therefore this QR is brand new!
                            Context context = getApplicationContext();
                            CharSequence text = "New QR! Adding to Database...";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            // add this QR to the database
                            // TODO: populate all fields
                            HashMap<String, Object> QRData = new HashMap<>();
                            QRData.put("Score", qr.getScore()); // use a manual score to test, 1234?


                            docRef.set(QRData); // set the data!
                            // could include success/failure listener?

                        }

                    } else {
                        // document query was not successful
                        Context context = getApplicationContext();
                        CharSequence text = "ERROR: query failed!";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            // TODO: Record logic. This is what remains of Michelle's work (untested?)
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

                RecordDB.document(recordID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // task is a document query
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                            } else {
                                HashMap<String, Object> recordData = new HashMap<>();
                                recordData.put("User", myAccount.getUsername());
                                recordData.put("QR", qr.getHash());

                                RecordDB.document(recordID).set(recordData);
                                RecordDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                                            FirebaseFirestoreException error) {
                                    }
                                });

                                //====== Add Record to user ======//
                                CollectionReference AccountDB = db.collection("AccountDB");
                                AccountDB.document(myAccount.getUsername())
                                        .collection("My QR Records").document(recordID).set(recordData);
                            }
                        }
                    }
                });
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
        Intent intent = new Intent(this, MainActivity.class);
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
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        Intent intent = new Intent(this, ScannedByActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to Comments activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToComments(View view) {
        Intent intent = new Intent(this, CommentsActivity.class);
        startActivity(intent);
    }




}