package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.Toast;


import com.example.qradventure.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Startup Activity
 */
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    BottomNavigationView navbarMenu;
    ActivityMainBinding binding;



    /**
     * **TEMP** logs into a default test account
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Main Activity");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        try {
            binding.navbarMenu.setItemIconTintList(null);
        }
        catch (Exception e)
        {
            Log.d("logs", e.toString());
        }
        setContentView(binding.getRoot());
        replaceFragment(new LocationFragment());

        binding.navbarMenu.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.my_account:

                    replaceFragment(new AccountFragment());
                    break;
                case R.id.leaderboards:
                    replaceFragment(new LeaderboardFragment());
                    break;
                case R.id.scan:
                    Intent intent3 = new Intent(getApplicationContext(), ScanActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.search_players:
                    replaceFragment(new SearchPlayersFragment());
                    break;
                case R.id.map:
                    replaceFragment(new LocationFragment());
                    break;
            }
            return true;

        });

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // DUMMY TEST ACCOUNT
        // TODO: DELETE
        Account account = new Account("Default Test Account", "temp", "temp", "temp", "temp");
        CurrentAccount.setAccount(account);

        //Get local android device ID
        String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        // ====== Query for accounts with matching device_id field ======
        // send to registration if no matching documents exist
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
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                // disable backward navigation to this activity
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
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
                                    TEMPConstructRecords();
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
     * **TEMPORARY**
     * Method that holds the logic to reconstruct Account records.
     */
    public void TEMPConstructRecords() {
        /*
         * TODO: DELETE WHEN QUERY HANDLER OPERATIONAL
         * get records for logged in account.
         * Huey's comments:
         * // The following is a query to grab all the QR codes that the player has added in their account
         * This is really long because this is two calls to the firebase db
         * Query steps:
         * 1.) I need to grab all the QR codes added by the user in the AccountDB collection
         * 2.) I need to grab all the scores by those QR codes in the QRDB
         * 3.) combine those two to create a new QR object and store it on the singleton account class
         */
        Account account = CurrentAccount.getAccount();
        try {
            Log.d("logs", account.getUsername());
            db.collection("AccountDB").document(CurrentAccount.getAccount().getUsername()).collection("My QR Records")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
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
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        // ====== Records reconstruction complete ======

    }

    // This transitions between fragments
    // Handles animations as well
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_layout,fragment);

        fragmentTransaction.commit();
    }
    /**
     * Sends to edit info activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToEditInfo(View view) {
        Intent intent = new Intent(this, EditInfoActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to StatusQR activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToStatusQR(View view) {
        Intent intent = new Intent(this, StatusQRActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to LoginQR activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToLoginQR(View view) {
        Intent intent = new Intent(this, LoginQRActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to MyStats activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToMyStats(View view) {
        Intent intent = new Intent(this, MyStatsActivity.class);

        startActivity(intent);
    }

    /**
     * Sends to MyCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToMyCodes(View view) {
        Intent intent = new Intent(this, MyCodesActivity.class);
        startActivity(intent);
    }
    /**
     * Sends to account activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToAccount(View view) {
        /**
         * TEMP: intent to MapActivity
         * Due to asynchronous query execution, reconstructing records is not done soon enough.
         * to "hide" the problem, temporarily send to MapActivity instead of AccountActivity.
         * This gives the query enough time to finish and display properly!
         */
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        // disable backward navigation to this activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    /**
     * ** TEMPORARY ** TODO: DELETE
     * Sends to login activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void TEMPgoToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}