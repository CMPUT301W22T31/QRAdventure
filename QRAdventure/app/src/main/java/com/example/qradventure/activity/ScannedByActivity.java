package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.qradventure.activity.ProfileActivity;

import java.util.ArrayList;


/**
 * Activity that displays a list of all the players that have scanned a particular QR code.
 */
public class ScannedByActivity extends AppCompatActivity {
    ListView playerListView;
    ArrayList<String> playerNames;
    ArrayAdapter<String> usernameAdapter;

    /**
     * Sets display views
     * Initializes onClickListeners
     * @param savedInstanceState - Unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_by);

        // load data from intent
        ArrayList<String> players = (ArrayList<String>)getIntent().getSerializableExtra("NAMES");

        // add data to an array
        playerNames = new ArrayList<>();
        for (int i = 0; i < players.size(); i++){
            playerNames.add(players.get(i));
        }

        // Initialize data, listview, adapter
        playerListView = findViewById(R.id.username_list);
        usernameAdapter = new ArrayAdapter<>(this, R.layout.username_list, playerNames);
        playerListView.setAdapter(usernameAdapter);

        // Enable listview on click listener
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name = playerNames.get(position);
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