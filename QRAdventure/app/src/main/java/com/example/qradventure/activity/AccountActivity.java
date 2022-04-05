package com.example.qradventure.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;


// ALL PROFILE ICONS WERE IMPORTED FROM SVGREPO.COM UNDER THE CC0 License

/**
 * Activity where the logged in player can manage their account
 * Displays number of codes scanned, total score, highest and lowest score
 * Leads to account's codes, stats, unique login code and status code
 */
public class AccountActivity extends AppCompatActivity {
    Account account;
    Dialog profileChoiceDialog;
    CardView profileCard;
    ImageView profilepic;
    BottomNavigationView navbar;
    FusedLocationProviderClient fusedLocationProviderClient;
    String content = null; // For getting QR content. needs to be global for the mock class

    /**
     * Sets layout and Enables navbar
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        profilepic = findViewById(R.id.profile_pic);
        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the account from the singleton
        account = CurrentAccount.getAccount();
        Log.d("bruh", "account test:");
        if (account == null) {
            Log.d("bruh", "account is null ");
        }
        else {
            Log.d("bruh", "account NOT null ");
        }
        // set up the profile pic chosen by the user
        profileCard = findViewById(R.id.profile_card);
        setUpProfilePic();
        //Log.d("logs", "Current location: " + account.getLocation().toString());

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

                    scanner.scan(AccountActivity.this);

                    break;
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(AccountActivity.this,
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
                            ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        }
                    break;
                case R.id.my_account:
                    // already on this activity. Do nothing.
                    break;
            }
            return false;
        });

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
     * On resume, display all the textviews.
     * So if text data changes after returning TO this activity, the views are updated.
     */
    @Override
    protected void onResume() {
        super.onResume();

        account = CurrentAccount.getAccount();

        // Give info to textviews to display
        try {
            // get textviews
            TextView displayTotalScore = findViewById(R.id.total_score);
            TextView displayCodesScanned = findViewById(R.id.codes_scanned);
            TextView displayLowestQR = findViewById(R.id.lowest_qr);
            TextView displayHighestQR = findViewById(R.id.highest_qr);
            TextView displayUsername = findViewById(R.id.text_owner);
            TextView displayEmail = findViewById(R.id.user_email);
            TextView displayPhoneNumber = findViewById(R.id.user_phone_number);

            // set textviews
            String username = account.getUsername();
            String email = account.getEmail();
            String phoneNumber = account.getPhoneNumber();
            displayUsername.setText(username);
            displayEmail.setText(email);
            displayPhoneNumber.setText(phoneNumber);

            displayTotalScore.setText(detailFormatter(account.getTotalScore()));
            displayCodesScanned.setText(detailFormatter(account.getTotalCodesScanned()));
            displayLowestQR.setText(detailFormatter(account.getLowestQR()));
            displayHighestQR.setText(detailFormatter(account.getHighestQR()));

        }
        catch (Exception e) {
            Log.d("logs", "Something went wrong while displaying!!! ");
        }
    }

    /**
     * Changes the format of the number for readability
     * @param number
     *      The number to change the format of
     * @return the changed value as a String
     */
    private String detailFormatter(Number number) {
        // if the number is too big, put it in this format
        // for example, if the user has 1345 qr's scanned
        // it would say 1.34k in the page
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.00").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    /**
     * Sends to edit info activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToEditInfo(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_choice_dialogue ,null);
        alertDialogBuilder.setView(dialogView);

        profileChoiceDialog = alertDialogBuilder.create();
        profileChoiceDialog.show();

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
        Intent intent = new Intent(this, StatsActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USERNAME), account.getUsername());
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
            content = "";

        if (  (!content.equals("QRSTATS-test-1-2-3-4") && !content.equals("QRLOGIN-b4048ce9c5919adf")))
            content = result.getContents();

        if (content.contains("QRSTATS-")) {
            Intent intent = new Intent(AccountActivity.this, StatsActivity.class);

            // extract the username from QR content, and add it to intentExtra
            String username = content.split("-")[1];
            intent.putExtra(getString(R.string.EXTRA_USERNAME), username);

            // start activity
            startActivity(intent);

        } else if (content.contains("QRLOGIN-")) {
            QueryHandler q = new QueryHandler();
            String deviceID = content.toString().split("-")[1];
            q.getLoginAccount(deviceID, new Callback() {
                Intent intent = new Intent(AccountActivity.this, AccountActivity.class);
                @Override
                public void callback(ArrayList<Object> args) {
                    DocumentReference docRef = db.collection("AccountDB").document(account.getUsername());
                    docRef.update("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }
            });
        } else if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
                Intent intent = new Intent(AccountActivity.this, PostScanActivity.class);
                intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
                startActivity(intent);
        } else {
            String text = "You have already scanned that QR";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }

    void setUpProfilePic() {
        switch (account.getProfileIndex()) {
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

    // profile choosing
    public void choseTurtle(View view) {
        profilepic.setBackgroundResource(R.drawable.ic_turtle);
        account.setProfileIndex(0);
        profileCard.setCardBackgroundColor(Color.parseColor("#4361EE"));
        QueryHandler updateProfile = new QueryHandler();

    }
    public void choseFish(View view) {
        profilepic.setBackgroundResource(R.drawable.ic_fish);
        account.setProfileIndex(1);
        profileCard.setCardBackgroundColor(Color.parseColor("#3A0CA3"));
        QueryHandler updateProfile = new QueryHandler();
        updateProfile.editProfilePic(1);
    }
    public void choseButterfly(View view) {
        profilepic.setBackgroundResource(R.drawable.ic_butterfly);
        account.setProfileIndex(2);
        profileCard.setCardBackgroundColor(Color.parseColor("#a8dadc"));
        QueryHandler updateProfile = new QueryHandler();
        updateProfile.editProfilePic(2);
    }
    public void choseLadyBug(View view) {
        profilepic.setBackgroundResource(R.drawable.ic_ladybug);
        account.setProfileIndex(3);
        profileCard.setCardBackgroundColor(Color.parseColor("#b5179e"));
        QueryHandler updateProfile = new QueryHandler();
        updateProfile.editProfilePic(3);
    }
    public void choseCrocodile(View view) {
        profilepic.setBackgroundResource(R.drawable.ic_crocodile);
        account.setProfileIndex(4);
        profileCard.setCardBackgroundColor(Color.parseColor("#457b9d"));
        QueryHandler updateProfile = new QueryHandler();
        updateProfile.editProfilePic(4);
    }
    public void choseDuck(View view) {
        profilepic.setBackgroundResource(R.drawable.ic_duck);
        account.setProfileIndex(5);
        profileCard.setCardBackgroundColor(Color.parseColor("#2a9d8f"));
        QueryHandler updateProfile = new QueryHandler();
        updateProfile.editProfilePic(5);
    }
}



