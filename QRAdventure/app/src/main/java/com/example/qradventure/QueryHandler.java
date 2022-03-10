package com.example.qradventure;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryHandler {

    FirebaseFirestore db;

    public QueryHandler(){
        FirebaseFirestore.getInstance();
    }


    public void getOthersScanned(QR qr, QueryCallback myCallback){

        db = FirebaseFirestore.getInstance();



        Task<QuerySnapshot> task = db.collection("RecordDB").whereEqualTo("QR", qr.getHash())
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String recordID;
                ArrayList<String> othersScanned = new ArrayList<>();

                if (task.isSuccessful()){
                    // Start list activity with the accounts

                    for (QueryDocumentSnapshot doc : task.getResult()){
                        recordID = doc.getId();
                        Log.d("RECORD:", recordID);
                        othersScanned.add(recordID);
                    }

                    myCallback.callback(othersScanned);

                }else{
                    Log.d("SUCCESS:", "NO");

                }

            }
        });

    }


}
