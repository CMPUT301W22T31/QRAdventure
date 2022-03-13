package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Activity that displays a list of all the players that have scanned a particular QR code.
 */
public class ScannedByActivity extends AppCompatActivity {
    String hash;
    ArrayList<String> players;
    ArrayAdapter<String> adapter;
    ListView playerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_by);
        setTitle("Scanned By:");

        playerList = findViewById(R.id.player_list);
        players = (ArrayList<String>)getIntent().getSerializableExtra("PLAYERS");

        adapter = new PlayerListAdapter(this, players);

        playerList.setAdapter(adapter);



    }
}