package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Activity that displays the stats of a user.
 * Received the username via intent
 * Leaf activity - no navbar
 */
public class StatsActivity extends AppCompatActivity {
    String username;
    int totalScore;
    int scanCount;
    int bestQR;
    String LOG = "Stats Log";

    /**
     * Controls calls to get stats and set textviews
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // get username via intent, set textview
        username = getIntent().getStringExtra(getString(R.string.EXTRA_USERNAME));
        TextView tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        // Set basic player stats
        SetPlayerStats();

        // set percentile for High Score -- "TotalScore"
        //calculatePercentile("TotalScore", totalScore)

        // set percentile for Scan Count -- "scanCount"


        // set percentile for Best QR -- "bestQR"
    }

    /**
     * Uses QueryHandler and Callback to get player stats
     * Sets relevant textviews
     */
    public void SetPlayerStats() {
        QueryHandler qh = new QueryHandler();
        qh.queryPlayerStats(username, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {

                // get fields (cast from long to int)
                totalScore = ((Number) args.get(0)).intValue();
                scanCount = ((Number) args.get(1)).intValue();
                bestQR = ((Number) args.get(2)).intValue();

                // get textviews
                TextView tvTotalScore = findViewById(R.id.tvStatsTotalScore);
                TextView tvScanCount = findViewById(R.id.tvStatsTotalScans);
                TextView tvBestQR = findViewById(R.id.tvStatsBestQR);

                // set textviews
                tvTotalScore.setText(String.valueOf(totalScore));
                tvScanCount.setText(String.valueOf(scanCount));
                tvBestQR.setText(String.valueOf(bestQR));

            }
        });
    }



    /**
     * Uses QueryHandler and Callback to get your percentile among all players
     * Note: egregious rounding - this serves only as an estimate!
     * @param field - (String) Field over which to filter your percentile
     */
    public void calculatePercentile(String field, int fieldValue) {
        QueryHandler qh = new QueryHandler();

        // ===== Stacked callbacks =====
        // Step 1) get count of players with worse scores
        qh.countLowerScores(field, fieldValue, new Callback() {
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


}