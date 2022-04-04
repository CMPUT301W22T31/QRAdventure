package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Activity that displays the stats of a user.
 * Receives the username via intent
 * Leaf activity - no navbar
 */
public class StatsActivity extends AppCompatActivity {
    String username;
    int totalScore;
    int scanCount;
    int bestQR;

    /**
     * Initiates call to display stats on textviews
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // get username via intent (using global string); set textview
        username = getIntent().getStringExtra(getString(R.string.EXTRA_USERNAME));
        TextView tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        // Set basic player stats
        SetPlayerStats();
    }

    /**
     * Uses QueryHandler and Callback to get player stats
     * Sets relevant textviews
     * Further calls methods to display percentiles (requires user scores)
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

                // get and set textviews related to rank (percentile)
                calculatePercentile("TotalScore", totalScore);
                calculatePercentile("scanCount", scanCount);
                calculatePercentile("bestQR", bestQR);

            }
        });
    }



    /**
     * Uses QueryHandler and Callback to get your percentile among all players
     * Note: egregious rounding - this serves only as an estimate!
     * @param field - (String) Field over which to filter your percentile
     * @param fieldValue - (int) value which to determine the percentile of
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
                        formatMyPercentile(percentile, field);
                    }
                });
            }
        });
    }

    /**
     * Formats a percentile for display by correcting suffix (50th, 51st, 53rd, etc)
     * Sets the corresponding textview text
     * @param percentile - (int) number to display
     * @param field - (string) field of rank, dictates which textview to set
     */
    public void formatMyPercentile(int percentile, String field) {
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

        // get & set the respective textview based on field
        if (field == "TotalScore") {
            TextView tvRank = findViewById(R.id.tvRankTotalScore);
            tvRank.setText(percString);
        } else if (field == "scanCount") {
            TextView tvRank = findViewById(R.id.tvRankTotalScans);
            tvRank.setText(percString);
        } else {
            TextView tvRank = findViewById(R.id.tvRankBestQR);
            tvRank.setText(percString);
        }


    }


}