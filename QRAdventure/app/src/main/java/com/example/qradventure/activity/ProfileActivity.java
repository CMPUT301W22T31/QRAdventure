package com.example.qradventure.activity;

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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qradventure.model.Callback;
import com.example.qradventure.model.CurrentAccount;
import com.example.qradventure.utility.LocationGrabber;
import com.example.qradventure.model.QueryHandler;
import com.example.qradventure.R;
import com.example.qradventure.model.Account;
import com.example.qradventure.model.QR;
import com.example.qradventure.model.Record;
import com.example.qradventure.model.scanner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


/**
 * Activity displaying the profile of any player. Anyone can access this activity.
 */
public class ProfileActivity extends AppCompatActivity {
    private String username;
    BottomNavigationView navbar;
    FirebaseFirestore db;
    FusedLocationProviderClient fusedLocationProviderClient;
    Account account;
    CardView profileCard;
    ImageView profilepic;

    String content = null;

    /**
     * Gets and displays fields of a player's profile
     * Enables navbar
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();

        Button deleteButton = findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.INVISIBLE);

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

        navbar.setVisibility(View.VISIBLE);

        // display delete button if owner
        String ownerRes = intent.getStringExtra("Owner");
        if (ownerRes != null) {
            deleteButton.setVisibility(View.VISIBLE);
            navbar.setVisibility(View.INVISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryHandler query = new QueryHandler();
                query.deleteAccount(username);

                Intent doneIntent = new Intent(ProfileActivity.this, SearchPlayersActivity.class);
                doneIntent.putExtra("Owner", "Owner");
                startActivity(doneIntent);
            }
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

    /**
     * Sends to ViewCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToViewCodes(View view) {
        Intent intent = getIntent();
        Intent viewCodesIntent = new Intent(this, ViewCodesActivity.class);
        viewCodesIntent.putExtra(getString(R.string.EXTRA_USERNAME), username);
        String ownerRes = intent.getStringExtra("Owner");
        if (ownerRes != null){
            viewCodesIntent.putExtra("Owner", "Owner");
        }
        startActivity(viewCodesIntent);
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
        if (email.equals("")) {
            email = "N/A";
        }
        if (phone.equals("")) {
            phone = "N/A";
        }
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvTotalScore = findViewById(R.id.tvStatsTotalScore);

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
     * This method is copied into every activity which we can clock the scannable button from
     *
     *
     * Citation for using the Scanning library
     * Website:https://androidapps-development-blogs.medium.com
     * link:https://androidapps-development-blogs.medium.com/qr-code-scanner-using-zxing-library-in-android-fe667862feb7
     * authir: Golap Gunjun Barman, https://androidapps-development-blogs.medium.com/
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (content == null){
            super.onActivityResult(requestCode, resultCode, data);
        }
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity

        if (content == null)
            content = result.getContents();

        if (account == null)
            account = CurrentAccount.getAccount();

        if (content.contains("QRSTATS-")) {
            Intent intent = new Intent(ProfileActivity.this, StatsActivity.class);

            // extract the username from QR content, and add it to intentExtra
            String username = content.split("-")[1];
            intent.putExtra(getString(R.string.EXTRA_USERNAME), username);

            // start activity
            startActivity(intent);

        }
        else if (content.contains("QRLOGIN-")) {
            QueryHandler q = new QueryHandler();
            String deviceID = content.toString().split("-")[1];
            q.getLoginAccount(deviceID, new Callback() {
                Intent intent = new Intent(ProfileActivity.this, AccountActivity.class);
                @Override
                public void callback(ArrayList<Object> args) {
                    DocumentReference docRef = db.collection("AccountDB").document(account.getUsername());
                    docRef.update("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }
            });
        }

        else if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
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