package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


/**
 * Activity displaying the profile of any player. Anyone can access this activity.
 */
public class ProfileActivity extends AppCompatActivity {
    private String username;
    BottomNavigationView navbar;
    FusedLocationProviderClient fusedLocationProviderClient;
    Account account;
    CardView profileCard;
    ImageView profilepic;


    /**
     * Gets and displays fields of a player's profile
     * Enables navbar
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // unpack intent to get account username
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.EXTRA_USERNAME));
        Account account = CurrentAccount.getAccount();

        // set up the profile pic chosen by the user
        profileCard = findViewById(R.id.other_profile_card);
        profilepic = findViewById(R.id.other_profile_pic);

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // ====== Enable navbar functionality ======
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
                    Log.d("check", "YES WORKING???");
                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.scan:
                    // Use IntentIntegrator to activate camera
                    scanner.scan(ProfileActivity.this);
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(ProfileActivity.this,
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
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }
                    break;
            }
            return false;
        });

        // ====== query DB for display fields ======
        QueryHandler query = new QueryHandler();
        query.getProfile(username, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {
                String email = (String) args.get(0);
                String phone = (String) args.get(1);
                Long totalScore = (Long)args.get(2);
                Long retrievedProfileIndex = (Long)args.get(3);

                // if the user has a profile pic set up then set the profile pic to be that
                if (retrievedProfileIndex != null) {
                    setUpProfilePic(retrievedProfileIndex.intValue());
                }
                else {
                    setUpProfilePic(0); // just set it to the turtle pic
                }

                setTextViews(username, email, phone, totalScore.toString());
            }
        });
    }

    void setUpProfilePic(Integer profileIndex) {
        switch (profileIndex) {
            case 0:
                profilepic.setBackgroundResource(R.drawable.ic_turtle);
                profileCard.setCardBackgroundColor(Color.parseColor("#4361EE"));
                break;
            case 1:
                profilepic.setBackgroundResource(R.drawable.ic_fish);
                profileCard.setCardBackgroundColor(Color.parseColor("#3A0CA3"));
                break;
            case 2:
                profilepic.setBackgroundResource(R.drawable.ic_butterfly);
                profileCard.setCardBackgroundColor(Color.parseColor("#a8dadc"));
                break;
            case 3:
                profilepic.setBackgroundResource(R.drawable.ic_ladybug);
                profileCard.setCardBackgroundColor(Color.parseColor("#b5179e"));
                break;
            case 4:
                profilepic.setBackgroundResource(R.drawable.ic_crocodile);
                profileCard.setCardBackgroundColor(Color.parseColor("#457b9d"));
                break;
            case 5:
                profilepic.setBackgroundResource(R.drawable.ic_duck);
                profileCard.setCardBackgroundColor(Color.parseColor("#2a9d8f"));
                break;
        }
    }

        /**
         * Sends to ViewCodes activity. Called when respective button is clicked.
         * @param view: unused
         */
    public void goToViewCodes(View view) {
        Intent intent = new Intent(this, ViewCodesActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USERNAME), username);
        startActivity(intent);
    }

    /**
     * Updates the textviews to display user data
     * @param name - account username
     * @param email - account email
     * @param phone - account phone #
     * @param score - account score
     */
    public void setTextViews(String name, String email, String phone, String score) {
        // get reference to the textviews
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvTotalScore = findViewById(R.id.tvTotalScore);

        // set textview text
        tvUsername.setText(name);
        tvPhone.setText(phone);
        tvEmail.setText(email);
        tvTotalScore.setText(score);
    }
    /**
     * Grabs location of user before entering maps activity
     */
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
        Account account = CurrentAccount.getAccount();

        if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
            Intent intent = new Intent(ProfileActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);

        }else{
            String text = "You have already scanned that QR";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }


}