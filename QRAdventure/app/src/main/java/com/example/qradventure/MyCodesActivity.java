package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;


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

import java.util.ArrayList;


/**
 * Activity where logged in user can access and manage the codes they have scanned
 * NOTE: This is functionally different from ViewCodesActivity.
 */
public class MyCodesActivity extends AppCompatActivity {
    GridView qrList;
    FirebaseFirestore db;
    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_my_codes);
        setTitle("My Codes");
        qrList = findViewById(R.id.qr_list);

        Account myAccount = CurrentAccount.getAccount();

        Log.d("logs", "Logged in as" + myAccount.getUsername());
        ArrayList<Record> accountRecords = myAccount.getMyRecords();
        //Log.d("logs", "" + accountRecords.size());
        for (Record record : accountRecords
        ) {
            Log.d("logs", record.getQRHash().substring(0, 4) + " " + record.getQRscore());
        }

        //String[] pts = {"23","342","34","34"};

        QRListAdapter qrListAdapter = new QRListAdapter(this, accountRecords);
        qrList.setAdapter(qrListAdapter);

//        Log.d("logs",""+ qrListAdapter.qrRecords.length);

        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), QRPageActivity.class);

                intent.putExtra("QRtitle", accountRecords.get(position).getQRHash().substring(0,4));
                intent.putExtra("QRHash", accountRecords.get(position).getQRHash());

                startActivity(intent);
            }
        });


        FloatingActionButton backButton = findViewById(R.id.button_back_to_Account);
        backButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                                              startActivity(intent);

                                          }
                                      });



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
                                try {
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
                                } catch (Exception e) {
                                    Log.d("logs", e.toString());
                                }
                                Log.d("logs", QRRecord);
                                myAccount.getMyRecords().remove(position);
                                qrListAdapter.notifyDataSetChanged();

                            }

                        }).setNegativeButton("No",null)
                .show();

                return true;

            }
        });

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
                    IntentIntegrator tempIntent = new IntentIntegrator(MyCodesActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
            }
            return false;
        });
    }
}







//        navbar = findViewById(R.id.navbar_menu);
//        navbar.setItemIconTintList(null);
//        navbar.setOnItemSelectedListener((item) -> {
//            switch (item.getItemId()) {
//                case R.id.leaderboards:
//                    Log.d("check", "WORKING???");
//                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
//                    startActivity(intent1);
//                    break;
//                case R.id.search_players:
//                    Log.d("check", "YES WORKING???");
//                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
//                    startActivity(intent2);
//                    break;
//                case R.id.scan:
//                    Intent intent3 = new Intent(getApplicationContext(), ScanActivity.class);
//                    startActivity(intent3);
//                    break;
//                case R.id.my_account:
//                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
//                    startActivity(intent4);
//                    break;
//            }
//            return false;
//        });
