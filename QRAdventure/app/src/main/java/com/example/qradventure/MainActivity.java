package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
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
    BottomNavigationView navbar;
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
                case R.id.location:
                    replaceFragment(new LocationFragment());
                    break;
            }
            return true;

        });

        // make sure to do this anytime an activity has a navbar
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);

        // Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // create a dummy account
        Account account = new Account("Default Test Account", "temp", "temp", "temp", "temp");
        // set CurrentAccount to this dummy account
        CurrentAccount.setAccount(account);

        CollectionReference userRecords = db.collection("AccountDB").document("Default Test Account").collection("My QR Records");

        // DELETE THIS LATER - Huey
        // As of right now we are logged in as Default Test Account on start up so I want to get all their records once the app boots up
        // This is so that we have the records to be displayed in pages.

        Log.d("logs","testing query");

        // get all the added QR's by the user and put them in a list


        // The following is a query to grab all the QR codes that the player has added in their account
        // This is really long because this is two calls to the firebase db
        // Query steps:
        // 1.) I need to grab all the QR codes added by the user in the AccountDB collection
        // 2.) I need to grab all the scores by those QR codes in the QRDB
        // 3.) combine those two to create a new QR object and store it on the singleton account class

        // This probably should be put in a query handle class...
        try {
            db.collection("AccountDB").document(account.getUsername()).collection("My QR Records")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String qrHash = (String) document.getData().get("QR");
                                    db.collection("QRDB").document(qrHash).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String qrScoreValue = (String) document.getData().get("Score");
                                                DocumentSnapshot document = task.getResult();
                                                String qrScore = "" + document.getData().get("Score");
                                                int qrValue = Integer.parseInt(qrScore);
                                                QR qr = new QR(qrHash, qrValue, null, null);
                                                Log.d("logs", qrHash + " " + qrValue);
                                                Record newRecord = new Record(account, qr);
                                                account.addRecord(newRecord);
                                            } else {
                                                Log.d("logs", "Cached get failed: ", task.getException());
                                            }
                                        }
                                    });
                                }
                            } else {
                                Log.d("logs", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

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
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to scan activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to search player activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToSearchPlayers(View view) {
        Intent intent = new Intent(this, SearchPlayersActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to leaderboard activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    /**
     * ** TEMPORARY **
     * Sends to login activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void TEMPgoToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}