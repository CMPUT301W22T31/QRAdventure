package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ProfileStatsActivity extends AppCompatActivity {


    TextView userHeaderStats;
    TextView totalScoreStats;
    TextView totalQRNumStats;
    TextView highestQRStats;
    TextView lowestQRStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_stats);


        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("QRSTATS")!= null)
        {
            String username = bundle.getString("QRSTATS").split("-")[1];
            String totalScore = bundle.getString("QRSTATS").split("-")[2];
            String totalScanned = bundle.getString("QRSTATS").split("-")[3];
            String highestQR = bundle.getString("QRSTATS").split("-")[4];
            String lowestQR = bundle.getString("QRSTATS").split("-")[5];

            userHeaderStats = findViewById(R.id.username_stats);
            totalScoreStats = findViewById(R.id.total_score_stats);
            totalQRNumStats = findViewById(R.id.total_qr_num);
            highestQRStats = findViewById(R.id.highest_qr_stats);
            lowestQRStats = findViewById(R.id.lowest_qr_stats);

            userHeaderStats.setText(username);
            totalScoreStats.setText(totalScore);
            totalQRNumStats.setText(totalScanned);
            highestQRStats.setText(highestQR);
            lowestQRStats.setText(lowestQR);
        }
    }
}