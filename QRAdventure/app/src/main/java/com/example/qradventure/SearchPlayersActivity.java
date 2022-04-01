package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


/**
 * Activity that lets anyone search for other players by their username
 */
public class SearchPlayersActivity extends AppCompatActivity {
    ListView playerListView;
    ArrayList<String> playerNames;
    ArrayAdapter<String> usernameAdapter;
    FirebaseFirestore db;
    Account account;
    BottomNavigationView navbar;
    FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Sets up UI elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_players);

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the account from the singleton
        account = CurrentAccount.getAccount();

        // Initialize data, listview, adapter
        playerListView = findViewById(R.id.username_list);
        playerNames = new ArrayList<>();
        usernameAdapter = new ArrayAdapter<>(this, R.layout.username_list, playerNames);
        playerListView.setAdapter(usernameAdapter);

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // set up on click listener
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String username = (String) playerListView.getItemAtPosition(pos);
                goToProfile(username);
            }
        });



        // enable navbar functionality
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        navbar.setOnItemSelectedListener((item) ->  {
            switch(item.getItemId()) {
                case R.id.leaderboards:
                    Log.d("check", "WORKING???");
                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.search_players:
                    // Already on this activity. Do nothing.
                    break;
                case R.id.scan:
                    // Use IntentIntegrator to activate camera
                    IntentIntegrator tempIntent = new IntentIntegrator(SearchPlayersActivity.this);
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
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(SearchPlayersActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // grab location of user before map activity starts
                        try {

                            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
                            locationGrabber.getLocation(this);
                            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent5);
                        }
                        catch (Exception e){
                            Log.d("logs", e.toString());
                        }
                    }
                    else {
                    }
                        ActivityCompat.requestPermissions(SearchPlayersActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    break;
            }
            return false;
        });
    }

    // Handles events on user's permissions on location.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        if (requestCode == 44) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.d("logs", "Grabbing location ");
            Log.d("logs", "Location before: " + account.getLocation().toString() );
            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
            locationGrabber.getLocation(this);
            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent5);
            Log.d("logs", "Location after: " + account.getLocation().toString() );
        }

    }

    /**
     * Sends app to the Profile Activity
     * Includes the username in the intent
     * @param username - username of selected profile
     */
    public void goToProfile(String username) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USERNAME), username);
        startActivity(intent);
    }

    /**
     * Called when search button is clicked
     * Query for an account with a username similar to the editText input
     * @param v - (unused)
     */
    public void queryUsername(View v) {
        // drop keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // clear current results
        playerNames.clear();
        usernameAdapter.notifyDataSetChanged();

        // get username from editText
        EditText tvUsername = findViewById(R.id.etUsername);
        String username = tvUsername.getText().toString();

        if (username.matches("")) {
            return;
        }

        QueryHandler query = new QueryHandler();


        query.playerSearch(username, new Callback() {
                    @Override
                    public void callback(ArrayList<Object> args) {

                        if (args.size() > 0){
                            for (Object o: args){
                                playerNames.add((String)o);

                            }
                            usernameAdapter.notifyDataSetChanged();
                        }else{
                                Context context = getApplicationContext();
                                CharSequence text = "No results found!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                        }



                    }
                });
    }


    /**
     * This method is called whenever a QR code is scanned. Takes the user to PostScanActivity
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
            if(content != null) {
                Intent intent = new Intent(SearchPlayersActivity.this, PostScanActivity.class);
                intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
                startActivity(intent);
            }
        }
}