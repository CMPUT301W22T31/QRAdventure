package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Class for holding all Querys to the firestore database.
 *
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
    public void getLoginAccount(String androidDeviceID, Callback callback){

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
                                ArrayList<Object> args = new ArrayList<Object>();
                                args.add(false);
                                callback.callback(args);
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
                                                                ArrayList<Object> args = new ArrayList<Object>();
                                                                args.add(true);
                                                                callback.callback(args);
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
                                                                                ArrayList<Object> args = new ArrayList<Object>();
                                                                                args.add(true);
                                                                                callback.callback(args);
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


    public void checkNameTaken(HashMap<String, Object> data, String username, Callback callback){

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("AccountDB").document(username);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // task is a document query
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, so username is taken!
                        ArrayList<Object> args = new ArrayList<Object>();
                        args.add(true);
                        callback.callback(args);


                    } else {
                        // Document does not exist, so username is available!
                        ArrayList<Object> args = new ArrayList<Object>();
                        args.add(false);
                        docRef.set(data);
                        callback.callback(args);
                    }

                } else {
                    // document query was not successful
                    int duration = Toast.LENGTH_SHORT;
                    Log.d(TAG, "get failed with ", task.getException());
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



    public void playerSearch(String username, Callback callback) {


        db = FirebaseFirestore.getInstance();
        db.collection("AccountDB")
                // query where document name starts with username
                .whereGreaterThanOrEqualTo("__name__", username)
                .whereLessThanOrEqualTo("__name__", username+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Object> args = new ArrayList<Object>();
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // no results returned, notify via toast
                                callback.callback(args);

                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        args.add(document.getId());
                                    } else {
                                        // crash protection & log. TODO: Revise
                                        Log.d("SearchPlayersActivity", "document dne");
                                    }
                                }

                                callback.callback(args);
                            }
                        } else {
                            // Query failed. Temporarily: display a toast
//                            Context context = getApplicationContext();
//                            CharSequence text = "Error: Query Failed!";
//                            int duration = Toast.LENGTH_SHORT;
//                            Toast toast = Toast.makeText(context, text, duration);
//                            toast.show();
                        }
                        // update listview adapter

                    }
                });
    }



    public void deleteRecord(Account myAccount, int position){


        db = FirebaseFirestore.getInstance();
        String QRRecord = myAccount.getUsername() + "-" + myAccount.getMyRecords().get(position).getQRHash();



        db.collection("AccountDB")
                .document(myAccount.getUsername())
                .collection("My QR Records")
                .document(QRRecord)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("logs", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("logs", "Error deleting document", e);
                    }
                });
        db.collection("QRDB")
                .document(myAccount.getMyRecords().get(position).getQRHash())
                .collection("Scanned By")
                .document(myAccount.getUsername())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("logs", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("logs", "Error deleting document", e);
                    }
                });
        db.collection("RecordDB")
                .document(QRRecord)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("logs", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("logs", "Error deleting document", e);
                    }
                });
    }



    public void getComments(String hash, Callback callback){

        db = FirebaseFirestore.getInstance();

        DocumentReference QRRef = db.collection("QRDB").document(hash);

        QRRef.collection("Comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Object> args = new ArrayList<Object>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String commentAuthor = document.getData().get("Author").toString();
                                String commentText = document.getData().get("Comment").toString();
                                Comment aComment = new Comment(commentAuthor, commentText);

                                args.add(aComment);
                            }
                            callback.callback(args);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addQR(QR qr, Callback callback) {
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("QRDB").document(qr.getHash());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // task is a document query
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList<Object> args = new ArrayList<Object>();
                    if (document.exists()) {
                        // Document exists, therefore QR already in DB!

                        args.add(true);

                        callback.callback(args);

                        // TODO: append CURRENT ACCOUNT to the list of players that have scanned this QR
                        //       use set(data, SetOptions.merge());

                    } else {
                        // Document does not exist, therefore this QR is brand new!

                        // add this QR to the database
                        // TODO: populate all fields
                        HashMap<String, Object> QRData = new HashMap<>();
                        QRData.put("Score", qr.getScore()); // use a manual score to test, 1234?



                        docRef.set(QRData); // set the data!
                        // could include success/failure listener?

                        args.add(false);
                        callback.callback(args);

                    }

                } else {
                    // document query was not successful
//                    Context context = getApplicationContext();
//                    CharSequence text = "ERROR: query failed!";
//                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context, text, duration);
//                    toast.show();
//                    Log.d(TAG, "get failed with ", task.getException());
                }
            }


        });

    }




    public void addRecord(String androidDeviceID, QR qr, Account myAccount, String recordID){
        db = FirebaseFirestore.getInstance();

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
                        recordData.put("UserScore", qr.getScore());

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


                        // Update Total user score
                        HashMap<String, Object> newUserData = new HashMap<String, Object>();
                        newUserData.put("E-mail", myAccount.getEmail());
                        newUserData.put("Phone Number", myAccount.getPhoneNumber());
                        newUserData.put("LoginQR", myAccount.getLoginQR());
                        newUserData.put("StatusQR", myAccount.getStatusQR());
                        newUserData.put("TotalScore", myAccount.getTotalScore());

                        newUserData.put("device_id", androidDeviceID);

                        AccountDB.document(myAccount.getUsername()).set(newUserData);

                    }
                }
            }
        });


    }

    public void getProfile(String username, Callback callback){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AccountDB").document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Object> args = new ArrayList<Object>();

                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String email = (String) doc.getData().get("E-mail");
                                String phone = (String) doc.getData().get("Phone Number");
                                Long totalScore = (Long) doc.getData().get("TotalScore");

                                args.add(email);
                                args.add(phone);
                                args.add(totalScore);
                                callback.callback(args);

                            } else {
                                // log: document dne
                                Log.d("logs", "Document does not exist!", task.getException());
                            }
                        } else {
                            // query failed
                            Log.d("logs", "Query Failed!", task.getException());
                        }
                    }
                });



    }


    public void loadRecords(String username, Callback callback){


        Account account = new Account(username, "", "", "", "");

        db.collection("AccountDB").document(username).collection("My QR Records")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Object> args = new ArrayList<Object>();


                            for (QueryDocumentSnapshot recordDoc : task.getResult()) {
                                // reconstruct the record
                                String hash = (String) recordDoc.get("QR");
                                String score =  "" + recordDoc.get("UserScore");
                                QR qr = new QR(hash, Integer.parseInt(score), null, null);
                                Record newRecord = new Record(account, qr);

                                args.add(newRecord);

                                // add the record and notify view!
                            }
                        } else {
                            // ERROR: QUERY FAILED
                            Log.d("VCActivity", "QUERY FAILED ", task.getException());
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
