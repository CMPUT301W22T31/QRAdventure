package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Putting Player data into HashMap
        HashMap<String, String> data = new HashMap<>();
        data.put("Email", "mlee1@ualberta.ca");
        data.put("Phone Number", "+1 780-123-4567");
        data.put("Username", "mlee1");
        data.put("device_id", "12393023");

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("Accounts");

        // test addition
        // Add data to a player document named mlee1
        collectionReference
                .document("mlee1")
                .set(data);

        Query accountsQuery = collectionReference
                .whereEqualTo("device_id", "12393023");  //Second argument should be device ID

        accountsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()) {
                        String userName = (String) doc.getData().get("Username");
                        String phoneNumber = (String) doc.getData().get("Phone Number");
                        String email = (String) doc.getData().get("Email");
                        Log.d("Testing", userName);
                    }
                }
                else {

                }
            }
        });


        // Update Firestore database
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
            }
        });

    }
}