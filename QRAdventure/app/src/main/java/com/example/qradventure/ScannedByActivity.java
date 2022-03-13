package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
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
    ArrayList<Long> scores;
    ArrayList<Pair<String, Long>> playerInfo;
    ArrayAdapter<Pair<String, Long>> adapter;
    ListView playerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_by);
        setTitle("Scanned By:");

        playerList = findViewById(R.id.player_list);
        players = (ArrayList<String>)getIntent().getSerializableExtra("NAMES");
        scores = (ArrayList<Long>)getIntent().getSerializableExtra("SCORES");
        playerInfo = new ArrayList<Pair<String, Long>>();

        // This isn't very efficient, should try and find a better way of doing this
        for (int i = 0; i < players.size(); i++){
            playerInfo.add(new Pair<String, Long>(players.get(i), scores.get(i)));
        }
        adapter = new PlayerListAdapter(this, playerInfo);

        playerList.setAdapter(adapter);

        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = players.get(i);
                goToProfile(name);
            }
        });


    }

    /**
     * Sends app to the Profile Activity
     * Includes the username in the intent
     * @param username - username of selected profile
     */
    public void goToProfile(String username) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USERNAME), username);
        startActivity(intent);
    }



}