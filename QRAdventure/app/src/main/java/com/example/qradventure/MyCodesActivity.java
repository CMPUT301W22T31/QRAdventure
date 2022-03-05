package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Activity where logged in user can access and manage the codes they have scanned
 * NOTE: This is functionally different from ViewCodesActivity.
 */
public class MyCodesActivity extends AppCompatActivity {
    GridView qrList;
    BottomNavigationView navbar;

    // The following are dummy values.. Please remove this once we have the qr system ready to go :P

    String[] QRname = {"Chocolate Bar", "Train Ad", "Random Wall", "Peanut Butter", "Video Game Ad",
            "Pokemon", "Team Seas", "OMG", "PlayStation", "Some Random", "Watches", "NO WAY", "Boxes"};

    String[] pts = {"102", "300", "240", "39", "111", "241", "34", "234", "12", "234", "112", "29", "73"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_codes);
        setTitle("My Codes");
        qrList = findViewById(R.id.qr_list);
        QRListAdapter qrListAdapter = new QRListAdapter(this, QRname, pts);
        qrList.setAdapter(qrListAdapter);

        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        navbar.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
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
                    Intent intent3 = new Intent(getApplicationContext(), ScanActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
            }
            return false;
        });
    }
}