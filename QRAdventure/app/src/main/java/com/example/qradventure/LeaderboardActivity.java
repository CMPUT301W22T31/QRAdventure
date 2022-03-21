package com.example.qradventure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


/**
 * Activity that displays a global leaderboard.
 */
public class LeaderboardActivity extends AppCompatActivity {
    BottomNavigationView navbar;
    PlayerPreview previewInfo;
    ArrayList<PlayerPreview> previewArray;
    ArrayAdapter<PlayerPreview> adapter;
    ListView playersListView;
    int currentFilter;
    String TAG = "Leaderboard_TAG";

    /**
     * Enables navbar and listview functionality
     * sets default leaderboard filter: High Score
     * @param savedInstanceState - (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Initialize & Link adapter
        previewArray = new ArrayList<PlayerPreview>();
        playersListView = findViewById(R.id.preview_list);
        adapter = new PlayerScoreAdapter(this, previewArray);
        playersListView.setAdapter(adapter);

        // Enable listview on click listener
        playersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name = previewArray.get(position).getUsername();
                goToProfile(name);
            }
        });

        // set default filter: High Score
        currentFilter = 0; // 0 is no filter. Need 0 on startup.
        displayHighScores(findViewById(R.id.buttonFilter1));

        // enable navbar functionality
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
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        });
    }

    /**
     * *Controller Method*
     * Validates filter selection; Calls query to display top players by TotalScore
     * @param v: button that called this method
     */
    public void displayHighScores(View v) {
        // This is filter 1. do nothing if already on this filter.
        if (currentFilter == 1) { return; }
        currentFilter = 1;
        String filter = "TotalScore";

        queryTopRanks(filter);

        int score = CurrentAccount.getAccount().getTotalScore();
        displayMyPercentile(filter, score);
    }

    /**
     * *Controller Method*
     * Validates filter selection; Calls query to display top players by BestQR
     * @param v: button that called this method
     */
    public void displayBestQR(View v) {
        // This is filter 1. do nothing if already on this filter.
        if (currentFilter == 2) { return; }
        currentFilter = 2;
        String filter = "bestQR";

        queryTopRanks(filter);

        int score = CurrentAccount.getAccount().getHighestQR();
        displayMyPercentile(filter, score);
    }

    /**
     * Validates filter selection
     * Controls calls for displaying top ranks and displaying your percentile
     * @param v: button that called this method
     */
    public void displayMostScanned(View v) {
        // This is filter 1. do nothing if already on this filter.
        if (currentFilter == 3) { return; }
        currentFilter = 3;
        String filter = "scanCount";

        queryTopRanks(filter);

        int score = CurrentAccount.getAccount().getTotalCodesScanned();
        displayMyPercentile(filter, score);
    }

    /**
     * Uses QueryHandler and Callback to get the top ranked players
     * Clears and then fills previewArray with the results
     * @param filter - (String) field over which to filter the top players
     */
    public void queryTopRanks(String filter) {
        QueryHandler qh = new QueryHandler();
        qh.getTopRanks(filter, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {
                // Use the array query callback provided
                // UNSAFE CAST - using wildcard <?> to help cast Object to PlayerPreview
                previewArray.clear();
                for (Object item : args){
                    previewInfo = (PlayerPreview) item;
                    previewArray.add(previewInfo);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Uses QueryHandler and Callback to get your percentile among all players
     * Note: egregious rounding - this serves only as an estimate!
     * @param filter - (String) Field over which to filter your percentile
     * @param score - (int) score to determine percentile of
     */
    public void displayMyPercentile(String filter, int score) {
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

                        // logs TODO: Delete
                        Log.d(TAG, "==== Percentile Calculation ====");
                        Log.d(TAG, "lower/total = " + countLower + " / " + countTotal);
                        Log.d(TAG, "Percentile = " + percentile);

                        // format percentile for display
                        String stringPercentile = percentile + "th Percentile!";

                        // set the textview!
                        TextView tvPercentile = findViewById(R.id.tvMyRank);
                        tvPercentile.setText(stringPercentile);
                    }
                });
            }
        });
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
        // get the QR contents, and send it to next activity

        String content = result.getContents();
        if(content != null) {
            Intent intent = new Intent(LeaderboardActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        }
    }

}