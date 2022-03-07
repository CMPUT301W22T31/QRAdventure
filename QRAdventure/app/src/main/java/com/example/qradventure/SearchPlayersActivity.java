package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Activity that lets anyone search for other players by their username
 */
public class SearchPlayersActivity extends AppCompatActivity {
    ListView playerListView;
    ArrayList<String> playerNames;
    ArrayAdapter<String> usernameAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_players);

        // Initialize data, listview, adapter
        playerListView = findViewById(R.id.username_list);
        playerNames = new ArrayList<>();
        usernameAdapter = new ArrayAdapter<>(this, R.layout.username_list, playerNames);
        playerListView.setAdapter(usernameAdapter);

        // set up on click listener
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String username = (String) playerListView.getItemAtPosition(pos);
                goToProfile(username);
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

    /**
     * Called when search button is clicked
     * Query for an account with a username similar to the editText input
     * @param v - (unused)
     */
    public void queryUsername(View v) {
        // clear current results
        playerNames.clear();
        usernameAdapter.notifyDataSetChanged();

        // get username from editText
        EditText tvUsername = findViewById(R.id.etUsername);
        String username = tvUsername.getText().toString();

        if (username.matches("")) {
            return;
        }

        // query AccountDB for documents that are similar to username
        db = FirebaseFirestore.getInstance();
        db.collection("AccountDB")
                // query where document name starts with username
                .whereGreaterThanOrEqualTo("__name__", username)
                .whereLessThanOrEqualTo("__name__", username+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // no results returned, notify via toast
                                Context context = getApplicationContext();
                                CharSequence text = "No results found!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        playerNames.add(document.getId());
                                    } else {
                                        // crash protection & log. TODO: Revise
                                        Log.d("SearchPlayersActivity", "document dne");
                                    }
                                }
                            }
                        } else {
                            // Query failed. Temporarily: display a toast
                            Context context = getApplicationContext();
                            CharSequence text = "Error: Query Failed!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        // update listview adapter
                        usernameAdapter.notifyDataSetChanged();
                    }
                });
    }



}