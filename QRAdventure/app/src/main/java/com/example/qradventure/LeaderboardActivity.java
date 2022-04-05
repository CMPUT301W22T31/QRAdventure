package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.function.Predicate;


/**
 * Activity that displays a global leaderboard.
 */
public class LeaderboardActivity extends AppCompatActivity {
    Account account;
    BottomNavigationView navbar;
    PlayerPreview previewInfo;
    ImageView firstPlace;
    ImageView secondPlace;
    ImageView thirdPlace;
    TextView filterSelected;


    ArrayList<PlayerPreview> previewArray;
    ArrayList<PlayerPreview> top3PreviewArray;
    ArrayAdapter<PlayerPreview> adapter;
    ListView playersListView;
    int fetchCount;
    int currentFilter;
    int colorToggleOn = Color.argb(120,255,255,255);
    int colorToggleOff = Color.argb(0,255,255,255);
    String TAG = "Leaderboard_TAG";
    FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Enables navbar, listview, and spinner functionality
     * sets default leaderboard filter: High Score
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the account from the singleton
        account = CurrentAccount.getAccount();

        top3PreviewArray = new ArrayList<PlayerPreview>();


        // ==== Initialize & Link adapter ====
        previewArray = new ArrayList<PlayerPreview>();
        playersListView = findViewById(R.id.preview_list);
        adapter = new PlayerScoreAdapter(this, previewArray);
        playersListView.setAdapter(adapter);

        // ==== Enable listview on click listener ====
        playersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name = previewArray.get(position).getUsername();
                goToProfile(name);
            }
        });

        // ==== Enable Spinner ====
        String[] spinnerChoices = new String[]{"Top 3", "Top 5", "top 10", "top 25"};
        Spinner spinner = (Spinner) findViewById(R.id.sizeSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>
                (this, R.layout.support_simple_spinner_dropdown_item, spinnerChoices);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(2); // default choice: Top 10!
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position) {
                    case 0:
                        // "Top 3" Selected
                        fetchCount = 3;
                        break;
                    case 1:
                        // "Top 5" selected
                        fetchCount = 5;
                        break;
                    case 2:
                        // "Top 10" selected
                        fetchCount = 10;
                        break;
                    case 3:
                        // "Top 25 selected"
                        fetchCount = 25;
                        break;
                    default:
                        // default top 10
                        fetchCount = 10;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        // ==== set default filter: top 10 from High Score ====
        fetchCount = 10;
        currentFilter = 0; // 0 is no filter. Need 0 on startup.
        displayHighScores(findViewById(R.id.buttonFilter1));

        // Enable navbar functionality
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        navbar.setOnItemSelectedListener((item) ->  {
            switch(item.getItemId()) {
                case R.id.leaderboards:
                    // Already on this activity. Do nothing.
                    break;
                case R.id.search_players:
                    Intent intent1 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.scan:
                    // Use IntentIntegrator to activate camera
                    IntentIntegrator tempIntent = new IntentIntegrator(LeaderboardActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(LeaderboardActivity.this,
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
                        ActivityCompat.requestPermissions(LeaderboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }
                    break;
            }
            return false;
        });
    }

    /**
     * simple method that "untoggles" all buttons (changes background)
     */
    public void untoggleButtonViews() {
        View v1 = findViewById(R.id.buttonFilter1);
        View v2 = findViewById(R.id.buttonFilter2);
        View v3 = findViewById(R.id.buttonFilter3);

        v1.setBackgroundColor(colorToggleOff);
        v2.setBackgroundColor(colorToggleOff);
        v3.setBackgroundColor(colorToggleOff);
    }

    /**
     * *Controller Method*
     * Validates filter selection; Calls query to display top players by TotalScore
     * @param v: button that called this method
     */
    public void displayHighScores(View v) {
        Log.d(TAG, "fetchcount = "+fetchCount);

        // This is filter 1. do nothing if already on this filter.
        if (currentFilter == 1 && previewArray.size() == fetchCount) { return; }

        // deselect all buttons, then toggle this button on
        untoggleButtonViews();
        v.setBackgroundColor(colorToggleOn);

        currentFilter = 1;
        String filter = "TotalScore";

        queryTopRanks(filter);

        int score = CurrentAccount.getAccount().getTotalScore();
        calculateMyPercentile(filter, score);
    }

    /**
     * *Controller Method*
     * Validates filter selection; Calls query to display top players by BestQR
     * @param v: button that called this method
     */
    public void displayBestQR(View v) {
        // This is filter 1. do nothing if already on this filter.
        if (currentFilter == 2 && previewArray.size() == fetchCount) { return; }

        // deselect all buttons, then toggle this button on
        untoggleButtonViews();
        v.setBackgroundColor(colorToggleOn);

        currentFilter = 2;
        String filter = "bestQR";

        queryTopRanks(filter);

        int score = CurrentAccount.getAccount().getHighestQR();
        calculateMyPercentile(filter, score);
    }

    /**
     * Validates filter selection
     * Controls calls for displaying top ranks and displaying your percentile
     * @param v: button that called this method
     */
    public void displayMostScanned(View v) {
        // This is filter 1. do nothing if already on this filter.
        if (currentFilter == 3 && previewArray.size() == fetchCount) { return; }

        // deselect all buttons, then toggle this button on
        untoggleButtonViews();
        v.setBackgroundColor(colorToggleOn);

        currentFilter = 3;
        String filter = "scanCount";

        queryTopRanks(filter);

        int score = CurrentAccount.getAccount().getTotalCodesScanned();
        calculateMyPercentile(filter, score);
    }

    /**
     * Uses QueryHandler and Callback to get the top ranked players
     * Clears and then fills previewArray with the results
     * @param filter - (String) field over which to filter the top players
     */
    public void queryTopRanks(String filter) {
        QueryHandler qh = new QueryHandler();

        qh.getTopRanks(filter, fetchCount, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {
                // Use the array query callback provided
                // UNSAFE CAST - using wildcard <?> to help cast Object to PlayerPreview
                ArrayList<PlayerPreview> top3people = new ArrayList<PlayerPreview>();
                previewArray.clear();
                for (Object item : args){
                    previewInfo = (PlayerPreview) item;
                    Log.d("bruh", previewInfo.getUsername());

                    previewArray.add(previewInfo);
                    top3people.add(previewInfo);
                }
                adapter.notifyDataSetChanged();

                firstPlace = findViewById(R.id.profile_pic_number_1);
                secondPlace = findViewById(R.id.profile_pic_number_2);
                thirdPlace = findViewById(R.id.profile_pic_number_3);
                TextView firstPlaceName = findViewById(R.id.first_place_name);
                TextView secondPlaceName = findViewById(R.id.second_place_name);
                TextView thirdPlaceName = findViewById(R.id.third_place_name);

                TextView firstPlaceScore = findViewById(R.id.first_place_score);
                TextView secondPlaceScore = findViewById(R.id.second_place_score);
                TextView thirdPlaceScore = findViewById(R.id.third_place_score);




                LeaderboardImageSetter leaderboardImageSetter = new LeaderboardImageSetter();
                ImageView[] imageViews = {firstPlace, secondPlace, thirdPlace};
                TextView[] usernameTextViews = {firstPlaceName, secondPlaceName, thirdPlaceName};
                TextView[] scoreTextViews = {firstPlaceScore, secondPlaceScore, thirdPlaceScore};

                //If there is less than three users, do not display the top three users
                Log.d("meme", thirdPlaceScore.getText().toString());
                if (args.size() < 3) {
                    //less than three players -> disable all display for top three players at top of leaderboard
                    ImageView crown = findViewById(R.id.crown);
                    crown.setVisibility(View.GONE);

                    TextView secondPlaceText = findViewById(R.id.second_place_number_text);
                    TextView thirdPlaceText = findViewById(R.id.third_place_number_text);
                    secondPlaceText.setVisibility(View.GONE);
                    thirdPlaceText.setVisibility(View.GONE);

                    firstPlaceName.setVisibility(View.GONE);
                    secondPlaceName.setVisibility(View.GONE);
                    thirdPlaceName.setVisibility(View.GONE);
                    firstPlaceScore.setVisibility(View.GONE);
                    secondPlaceScore.setVisibility(View.GONE);
                    thirdPlaceScore.setVisibility(View.GONE);
                    firstPlace.setVisibility(View.GONE);
                    secondPlace.setVisibility(View.GONE);
                    thirdPlace.setVisibility(View.GONE);

                }
                else {
                    for (int i = 0; i < 3; i++) {
                        leaderboardImageSetter.setImages(scoreTextViews[i], usernameTextViews[i], imageViews[i], top3people.get(i));
                    }
                }
            }
        });
    }


    /**
     * Uses QueryHandler and Callback to get your percentile among all players
     * Note: egregious rounding - this serves only as an estimate!
     * @param filter - (String) Field over which to filter your percentile
     * @param score - (int) score to determine percentile of
     */
    public void calculateMyPercentile(String filter, int score) {
        QueryHandler qh = new QueryHandler();

        // ===== Stacked callbacks =====
        // Step 1) get count of players with worse scores
        qh.countLowerScores(filter, score, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {
                int countLower = (int) args.get(0);

                // Step 2) Get total player count
                qh.countTotalPlayers(new Callback() {
                    @Override
                    public void callback(ArrayList<Object> args) {
                        int countTotal = (int) args.get(0);

                        // Step 3) Calculate percentile: 100 * (lower/total)
                        int percentile = (countLower*100) / countTotal;

                        // send this number to be formatted & displayed
                        formatMyPercentile(percentile);


                    }
                });
            }
        });
    }

    /**
     * Formats a percentile for display by correcting suffix (50th, 51st, 53rd, etc)
     * @param percentile - (int) number to display
     */
    public void formatMyPercentile(int percentile) {
        String percString = "";
        int ones = percentile % 10;
        int tens = percentile / 10;

        // generate suffix exhaustively; 0th - 100th inclusive
        if (ones == 1 && tens != 1) {
            percString = percentile + "st";
        } else if(ones == 2 && tens != 1) {
            percString = percentile + "nd";
        } else if(ones == 3 && tens != 1) {
            percString = percentile + "rd";
        } else {
            percString = percentile + "th";
        }

        // set the textview
        TextView tvPercentile = findViewById(R.id.tvMyRank);
        tvPercentile.setText(percString);
    }


    /**
     * Sends to profile activity. Called when a player is clicked.
     * @param username: Username of profile to display
     */
    public void goToProfile(String username) {
        // add username as intent extra; launch ProfileActivity
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USERNAME), username);
        startActivity(intent);
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
     * This method is called whenever a QR code is scanned.
     * Starts PostScanActivity with qr content as intent extra.
     * @param requestCode - (int) returned from camera activity result
     * @param resultCode - (int) returned from camera activity result
     * @param data - (Intent) returned from camera activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // get the QR contents, and send it to next activity or return

        Account account = CurrentAccount.getAccount();
        String content = result.getContents();

        if(content != null && !account.containsRecord(new Record(account, new QR(content)))) {
            Intent intent = new Intent(LeaderboardActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        } else {
            String text = "You have already scanned that QR";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }

}