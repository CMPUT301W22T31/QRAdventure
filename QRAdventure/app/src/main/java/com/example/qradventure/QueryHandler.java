package com.example.qradventure;

import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class for holding all Querys to the Firestore database.
 */
public class QueryHandler {

    FirebaseFirestore db;
    String TAG = "QueryHandler";

    public QueryHandler(){
        FirebaseFirestore.getInstance();
    }

    /**
     * Very large query for obtaining all the info for the current logged in user account
     *
     * @param androidDeviceID
     *      Device ID of the android device. Used to automatically log in the account
     *
     * @param callback
     *      Callback function used after the Query has completed
     */
    public void getLoginAccount(String androidDeviceID, AccountCallback callback){

        db = FirebaseFirestore.getInstance();

        db.collection("AccountDB")
                .whereEqualTo("device_id", androidDeviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            // query completed
                            // TODO: reformat this "for". Surely there's a better way than a for loop for 1 document??
                            if (task.getResult().size() == 0) {
                                // no documents found! go to registration
                                Log.d("logs", "doc dne!");
                                // disable backward navigation to this activity
                                callback.toActivity(false);
                            }
                            for(QueryDocumentSnapshot doc: task.getResult()) {
                                if (doc.exists()) {
                                    //Fetch the user account
                                    String email = (String) doc.getData().get("E-mail");
                                    String loginQR = (String) doc.getData().get("LoginQR");
                                    String phoneNumber = (String) doc.getData().get("Phone Number");
                                    String statusQR = (String) doc.getData().get("StatusQR");
                                    String username = (String) doc.getId();
                                    Log.d("logs", "doc exists + " + username);

                                    Account fetchedAccount =
                                            new Account(username, email, phoneNumber, loginQR, statusQR);
                                    CurrentAccount.setAccount(fetchedAccount);

                                    // Account reconstructed - need to reconstruct records
                                    Account account = CurrentAccount.getAccount();
                                    try {
                                        Log.d("logs", account.getUsername());
                                        db.collection("AccountDB").document(CurrentAccount.getAccount().getUsername()).collection("My QR Records")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().size() == 0) {
                                                                // no records found! Complete login.
                                                                callback.toActivity(true);
                                                            }
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String qrHash = (String) document.getData().get("QR");
                                                                db.collection("QRDB").document(qrHash)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot document = task.getResult();
                                                                                    String qrScore = "" + document.getData().get("Score");
                                                                                    int qrValue = Integer.parseInt(qrScore);
                                                                                    QR qr = new QR(qrHash, qrValue, null, null);
                                                                                    Log.d("logs", qrHash + " " + qrValue);
                                                                                    Record newRecord = new Record(account, qr);
                                                                                    account.addRecord(newRecord);
                                                                                } else {
                                                                                    // ERROR: Query failed!
                                                                                    Log.d("logs", "Cached get failed: ", task.getException());
                                                                                }

                                                                                callback.toActivity(true);
                                                                            }
                                                                        });
                                                            }

                                                        } else {
                                                            // ERROR: query unsuccessful
                                                            Log.d("logs", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                    catch (Exception e) {
                                        Log.d("OOPS", "Query failed");
                                    }
                                } else {
                                    // doc does not exist. I think this case is covered already, but just incase:
                                    Log.d("logs", "Document does not exist!");
                                }
                            }
                        } else {
                            // ERROR: query failed
                            Log.d("logs", "DeviceID query failed!", task.getException());
                        }
                    }
                });
    }

    /**
     * Gets all other players which have scanned a QR code and displays them in ScannedBy ACtivity
     * @param qrHash
     *      The QR code we are querying with
     * @param myCallback
     *      Callback function used after the query is done
     */
    public void getOthersScanned(String qrHash, QueryCallback myCallback){

        db = FirebaseFirestore.getInstance();

        Task<QuerySnapshot> task = db.collection("RecordDB").whereEqualTo("QR", qrHash)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String recordID;
                ArrayList<String> playerNames = new ArrayList<String>();
                ArrayList<Long> playerScores = new ArrayList<Long>();

                if (task.isSuccessful()){
                    // Start list activity with the accounts
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        recordID = doc.getId();
                        Log.d(TAG, recordID);
                        String accName = recordID.substring(0, recordID.indexOf('-'));
                        Map<String, Object> accData = doc.getData();
                        Long totalScore = (Long)accData.get("UserScore");

                        playerNames.add(accName);
                        playerScores.add(totalScore);
                    }
                    myCallback.callback(playerNames, playerScores);
                }else{
                    // ERROR: Task failed
                    Log.d(TAG, "QUERY FAILED");
                }

            }
        });
    }

    /**
     * Used for account login
     */
    public void LoginQuery(String androidDeviceID) {
        db = FirebaseFirestore.getInstance();
    }


}
