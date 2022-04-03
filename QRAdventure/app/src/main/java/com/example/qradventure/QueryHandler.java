package com.example.qradventure;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
        db = FirebaseFirestore.getInstance();
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


                                                                Blob imageBlob = (Blob)document.getData().get("ImageData");

                                                                /*
                                                                This needed to check if there is not an image since
                                                                anything in an inner class must be final. Can't use
                                                                null or else it will crash
                                                                */
                                                                byte[] imageData = "filler".getBytes();

                                                                try{
                                                                    imageData = imageBlob.toBytes();
                                                                }
                                                                catch(Exception e){// Just do nothing, since we want imageData to retain its dummy value

                                                                }

                                                                Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);


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

                                                                                    newRecord.setImage(image);

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

    /**
     * Checks if a certain name is taken and add the account if it is not taken
     * Used when an account is being created
     * informs the calling activity of the result
     *
     * @param data Data which will be set if username is not taken
     * @param username The name we are checking
     * @param callback Callback function
     */
    public void checkNameTaken(HashMap<String, Object> data, String username, Callback callback){

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
     * TODO: The scores this returns are only the scores of the QR. Not the player's sum score.
     * @param qrHash
     *      The QR code we are querying with
     * @param myCallback
     *      Callback function used after the query is done
     */
    public void getOthersScanned(String qrHash, QueryCallback myCallback){

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
                        Log.d(TAG, "accName = " + accName);
                        Log.d(TAG, "totalScore = "+ totalScore);

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
     * Takes a string and querys a search result based off of the string
     * @param username What we are searching for
     * @param callback Callback function
     */
    public void playerSearch(String username, Callback callback) {

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
                                // no results returned
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
                            // Query failed
                            Log.d(TAG, "playerSearch Unsuccessful!");
                        }

                    }
                });
    }


    /**
     * Deletes a record from the databnase
     * @param myAccount The account from which we are deleting the record
     * @param toDelete The record which is being deleted
     */
    public void deleteRecord(Account myAccount, Record toDelete){

        String QRRecord = myAccount.getUsername() + "-" + toDelete.getQRHash();

        HashMap<String, Object> newScore = new HashMap<String, Object>();
        newScore.put("TotalScore", myAccount.getTotalScore());

        db.collection("AccountDB").document(myAccount.getUsername())
                .update(newScore);

        db.collection("AccountDB")
                .document(myAccount.getUsername())
                .collection("My QR Records")
                .document(QRRecord)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("logs", "DocumentSnapshot successfully deleted!");

                        // update the account's bestQR & scanCount fields
                        updateBestQR();
                        updateScanCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("logs", "Error deleting document", e);
                    }
                });
        db.collection("QRDB")
                .document(toDelete.getQRHash())
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


    /**
     * Gets a list of comments for CommentsActivity
     * @param hash Hash of the QR we are getting the comments from
     * @param callback Callback function for the calling activity
     */
    public void getComments(String hash, Callback callback){

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


    /**
     * Adds a QR to the database
     * @param qr
     *      What we are adding
     * @param callback
     *      Callback function for the calling activity
     */
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
                        QRData.put("Score", qr.getScore()); // use a manual score to test, 1234

                        docRef.set(QRData); // set the data!
                        // could include success/failure listener?

                        args.add(false);
                        callback.callback(args);

                    }

                } else {
                    // Query failed
                    Log.d(TAG, "playerSearch Unsuccessful!");
                }
            }


        });

    }


    /**
     * Adds a new record to the database
     * @param androidDeviceID
     *      Device of the account which is being used
     * @param qr
     *      QR code of the record
     * @param myAccount
     *      Account of the record
     * @param toAdd
     *      The record we are adding
     */

    public void addRecord(String androidDeviceID, QR qr, Account myAccount, Record toAdd){
        db = FirebaseFirestore.getInstance();
        String recordID = toAdd.getID();

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

                        // put longitude and latitude of qr
                        if (qr.getGeolocation().size() != 0){
                            recordData.put("GeoHash",qr.getGeoHash());
                            recordData.put("Longitude", qr.getGeolocation().get(0));
                            recordData.put("Latitude", qr.getGeolocation().get(1));
                        }


                        if (toAdd.getImage() != null){
                            Bitmap image = toAdd.getImage();
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            byte[] imageData = out.toByteArray();
                            Blob imageBlob = Blob.fromBytes(imageData);
                            recordData.put("ImageData", imageBlob);

                        }

                        RecordDB.document(recordID).set(recordData);
                        RecordDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                                    FirebaseFirestoreException error) {
                            }
                        });

                        // update the account's bestQR & scanCount fields
                        updateBestQR();
                        updateScanCount();

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

    /**
     * Gets information about another players profile
     * @param username
     *      The profile we are querying for
     * @param callback
     *      Callback function for the main thread
     */
    public void getProfile(String username, Callback callback){

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

    /**
     * Querys for a users records
     * @param username
     *      Name of the account whos records we are getting
     * @param callback
     *      Callback function for the main thread
     */
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
                                Blob imageBlob = (Blob)recordDoc.getData().get("ImageData");



                                QR qr = new QR(hash, Integer.parseInt(score), null, null);
                                Record newRecord = new Record(account, qr);
                                if (imageBlob!=null){
                                    byte[] imageData = imageBlob.toBytes();
                                    Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                    newRecord.setImage(image);
                                }

                                args.add(newRecord);



                                // add the record and notify view!
                            }

                            callback.callback(args);
                        } else {
                            // ERROR: QUERY FAILED
                            Log.d("VCActivity", "QUERY FAILED ", task.getException());
                        }
                    }
                });




    }



    public void getNearbyQRs(ArrayList<Double> location, Callback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final double range = 10;

        // Get the account from the singleton
        Account account = CurrentAccount.getAccount();

        // Find QR's within 10km of user's location
        // References: https://firebase.google.com/docs/firestore/solutions/geoqueries#java_2

        final GeoLocation usersGeolocation = new GeoLocation(account.getLocation().get(1), account.getLocation().get(0));
        final double radiusInM = 10 * 1000;

        // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
        // a separate query for each pair. There can be up to 9 pairs of bounds
        // depending on overlap, but in most cases there are 4.
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(usersGeolocation, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("RecordDB")
                    .orderBy("GeoHash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        ArrayList<NearByQR> nearbyQRs = new ArrayList<NearByQR>();
                        ArrayList<Object> args = new ArrayList<Object>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                double lat = doc.getDouble("Latitude");
                                double lng = doc.getDouble("Longitude");


                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match

                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, usersGeolocation);
                                Log.d("hi", "nearby distance: " +distanceInM);
                                if (distanceInM <= radiusInM) {
                                    QR qr = new QR(doc.get("QR").toString());
                                    boolean scanned;
                                    Log.d("bruh",doc.getString("User").toString());
                                    if (doc.getString("User").equals(account.getUsername())){ // if its a qr by the user its been scanned
                                        scanned = true;
                                        Log.d("bruh", "has been scanned by user ");
                                    }
                                    else scanned = false;
                                    NearByQR nearByQR = new NearByQR(lng,lat, distanceInM,Integer.parseInt(doc.get("UserScore").toString()), scanned);
                                    nearbyQRs.add(nearByQR);
                                }
                            }
                        }

                        args.add(nearbyQRs);
                        callback.callback(args);
                    }
                });
    }

    /**
     * Queries for the top ranked players by a certain field (filter).
     * Callback returns an arraylist of PlayerPreview objects.
     * @param fieldFilter - field over which to rank players
     * @param fetchCount - number of ranks to fetch (top 3/5/10/25, etc)
     * @param callback - callback to return previewArray when query complete.
     */
    public void getTopRanks(String fieldFilter, int fetchCount, Callback callback) {

        ArrayList<Object> previewArray = new ArrayList<Object>();

        // query over accounts, returns top 5 documents by fieldFilter
        db.collection("AccountDB")
                .orderBy(fieldFilter, Query.Direction.DESCENDING)
                .limit(fetchCount)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int rank = 0;
                            for (QueryDocumentSnapshot accDocRef : task.getResult()) {
                                rank = rank + 1;
                                // get relevant preview data
                                String username = accDocRef.getId();
                                String score = "" + accDocRef.get(fieldFilter).toString();

                                // create preview and add to array
                                PlayerPreview newPreview = new PlayerPreview(username, score, rank);
                                previewArray.add(newPreview);
                            }
                            // outside for loop, callback the array
                            callback.callback(previewArray);

                        } else {
                            Log.d(TAG, "getTopRanks unsuccessful!: ", task.getException());
                        }
                    }
                });
    }
    /**
     * Performs simple query that returns the number of players whose
     * score is lower than the given score (over given fieldFilter)
     * Note: Can alter query to be inclusive/exclusive of given score. Opt for inclusive.
     * @param fieldFilter - field over which to rank players
     * @param callback - callback to return count to
     */
    public void countLowerScores(String fieldFilter, int score, Callback callback) {

        db.collection("AccountDB")
                .whereLessThanOrEqualTo(fieldFilter, score)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // create the array of 1 element; callback
                            ArrayList<Object> countArray = new ArrayList<Object>();
                            countArray.add(task.getResult().size());
                            callback.callback(countArray);
                        } else {
                            Log.d(TAG, "countLowerScores unsuccessful!: ", task.getException());
                        }
                    }
                });

    }

    /**
     * Performs simple query that returns the total number of Accounts
     * @param callback - callback to return count to
     */
    public void countTotalPlayers(Callback callback) {

        db.collection("AccountDB")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // create the array of 1 element; callback
                            ArrayList<Object> countArray = new ArrayList<Object>();
                            countArray.add(task.getResult().size());
                            callback.callback(countArray);

                        } else {
                            Log.d(TAG, "countTotalPlayers unsuccessful!: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Updates the "bestQR" field in the database for the CurrentAccount.
     * Should be called whenever records are added or deleted.
     */
    public void updateBestQR() {
        // need a reference to the account document
        DocumentReference accDocRef = db.collection("AccountDB")
                .document(CurrentAccount.getAccount().getUsername());

        // query for highest scoring record; update account's bestQR field
        accDocRef.collection("My QR Records")
                .orderBy("UserScore", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> updateData = new HashMap<String, Object>();
                            if (task.getResult().size() == 0) {
                                // Special case: User has no records!
                                updateData.put("bestQR", 0);
                                accDocRef.update(updateData);
                            }
                            for (QueryDocumentSnapshot recordDocRef : task.getResult()) {
                                if (recordDocRef.get("UserScore") != null) {
                                    // update account's bestQR field
                                    long bestQR = (long) recordDocRef.get("UserScore");
                                    updateData.put("bestQR", bestQR);
                                    accDocRef.update(updateData);
                                }
                            }
                        } else {
                            Log.d(TAG, "updateBestQR unsuccessful!: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Updates the "scanCount" field in the database for the CurrentAccount.
     * Should be called whenever records are added or deleted.
     */
    public void updateScanCount() {
        // need a reference to the account document
        DocumentReference accDocRef = db.collection("AccountDB")
                .document(CurrentAccount.getAccount().getUsername());

        // query for highest scoring record; update account's bestQR field
        accDocRef.collection("My QR Records")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // get count (result size; num of records!)
                            int count = task.getResult().size();

                            // update field
                            HashMap<String, Object> updateData = new HashMap<String, Object>();
                            updateData.put("scanCount", count);
                            accDocRef.update(updateData);

                        } else {
                            Log.d(TAG, "updateScanCount unsuccessful!: ", task.getException());
                        }
                    }
                });
    }

    public void getAmntScanned(QR qr , Callback callback) {

        db.collection("QRDB").document(qr.getHash()).collection("Scanned By")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // create the array of 1 element; callback
                            ArrayList<Object> countArray = new ArrayList<Object>();
                            countArray.add(task.getResult().size());
                            callback.callback(countArray);

                        } else {
                            Log.d(TAG, "Unsucessful query.", task.getException());
                        }
                    }
                });

    }

}
