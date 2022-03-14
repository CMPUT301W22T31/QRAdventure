package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    /**
     * Gets all the UI elements for displaying the QR codes and adds click listeners. Adds navbar
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_codes);
        setTitle("My Codes");
        qrList = findViewById(R.id.qr_list);

        Account myAccount = CurrentAccount.getAccount();

        ArrayList<Record> accountRecords = myAccount.getMyRecords();
        QRListAdapter qrListAdapter = new QRListAdapter(this, accountRecords);
        qrList.setAdapter(qrListAdapter);


        // ====== on click listener : intent to activity ======
        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent to QRPageActivity
                Intent intent = new Intent(getApplicationContext(), QRPageActivity.class);
                intent.putExtra("QRtitle", accountRecords.get(position).getQRHash().substring(0,4));
                intent.putExtra("QRHash", accountRecords.get(position).getQRHash());
                startActivity(intent);
            }
        });

        // ====== Back Button Logic ======
        FloatingActionButton backButton = findViewById(R.id.button_back_to_Account);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                             finish();
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
        if (content != null) {
            Intent intent = new Intent(MyCodesActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        }
    }




}
