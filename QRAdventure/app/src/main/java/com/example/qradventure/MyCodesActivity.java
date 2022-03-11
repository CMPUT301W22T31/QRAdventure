package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


/**
 * Activity where logged in user can access and manage the codes they have scanned
 * NOTE: This is functionally different from ViewCodesActivity.
 */
public class MyCodesActivity extends AppCompatActivity {
    GridView qrList;
    BottomNavigationView navbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_my_codes);
        setTitle("My Codes");
        qrList = findViewById(R.id.qr_list);

        Account myAccount = CurrentAccount.getAccount();

        Log.d("logs","Logged in as" + myAccount.getUsername());


        ArrayList<Record> accountRecords = myAccount.getMyRecords();
        //Log.d("logs", "" + accountRecords.size());
        for (Record record: accountRecords
             ) {
            Log.d("logs", record.getQRHash().substring(0,4) + " " + record.getQRscore());
        }

        //String[] pts = {"23","342","34","34"};

        QRListAdapter qrListAdapter = new QRListAdapter(this, accountRecords);
        qrList.setAdapter(qrListAdapter);

//        Log.d("logs",""+ qrListAdapter.qrRecords.length);

        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), QRPageActivity.class);
                intent.putExtra("QRtitle", accountRecords.get(position).getQRHash().substring(0,4));
                startActivity(intent);
            }
        });



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