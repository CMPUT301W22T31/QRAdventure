package com.example.qradventure;

import android.content.Context;
import android.security.identity.EphemeralPublicKeyNotFoundException;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This is an adapter for displaying a list of players, along with their score.
 * Currently just used for viewing players who have scanned the same QR code
 */

public class PlayerListAdapter extends ArrayAdapter<Pair<String, Long>> {



    Context context;


    private ArrayList<Pair<String, Long>> players;


    public PlayerListAdapter(Context context, ArrayList<Pair<String, Long>> players){
        super(context, 0, players);
        this.context = context;
        this.players = players;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;


        Pair<String, Long> accInfo = players.get(position);

        view = LayoutInflater.from(context).inflate(R.layout.player_list_entry, parent, false);

        TextView playerName = view.findViewById(R.id.other_player_name);
        TextView playerScore = view.findViewById(R.id.other_player_score);
        playerName.setText(accInfo.first);
        playerScore.setText(Long.toString(accInfo.second));


        return view;



    }
}
