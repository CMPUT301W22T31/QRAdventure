package com.example.qradventure;

import android.content.Context;
import android.security.identity.EphemeralPublicKeyNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlayerListAdapter extends ArrayAdapter<String> {

    Context context;


    private ArrayList<String> players;


    public PlayerListAdapter(Context context, ArrayList<String> players){
        super(context, 0, players);
        this.context = context;
        this.players = players;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;


        String name = players.get(position);

        view = LayoutInflater.from(context).inflate(R.layout.player_list_entry, parent, false);

        TextView playerName = view.findViewById(R.id.other_player_name);

        playerName.setText(name);

        return view;



    }
}
