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
    PlayerPreview previewInfo;
    ArrayList<PlayerPreview> previewArray;
    ArrayAdapter<PlayerPreview> adapter;
    ListView playersListView;

    /**
     * Sets display views
     * Initializes onClickListeners
     * @param savedInstanceState - Unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_by);
        setTitle("Scanned By:");

        // load data from intent
        ArrayList<String> players = (ArrayList<String>)getIntent().getSerializableExtra("NAMES");
        ArrayList<Long> scores = (ArrayList<Long>)getIntent().getSerializableExtra("SCORES");


        // add data to an array
        previewArray = new ArrayList<PlayerPreview>();
        for (int i = 0; i < players.size(); i++){
            previewInfo = new PlayerPreview(players.get(i), scores.get(i));
            previewArray.add(previewInfo);
        }

        // Initialize & Link adapter
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