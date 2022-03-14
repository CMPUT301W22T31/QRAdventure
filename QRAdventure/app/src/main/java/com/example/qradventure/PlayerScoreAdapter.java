package com.example.qradventure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying listviews with multiple components.
 */
public class PlayerScoreAdapter extends ArrayAdapter<PlayerPreview> {
    private ArrayList<PlayerPreview> players;
    private Context context;

    public PlayerScoreAdapter(Context context, ArrayList<PlayerPreview> data) {
        super(context, 0, data);
        this.players = data;
        this.context = context;
    }

    /**
     * Override default getView with a custom one - view from player_list_entry.
     * @param position - which item in the listview to generate
     * @param convertView -
     * @param parent - super
     * @return View - custom view to display
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.player_list_entry, parent, false);
        }

        PlayerPreview preview = players.get(position);

        // get and set the textviews
        TextView tvUsername = view.findViewById(R.id.other_player_name);
        TextView tvTotalScore = view.findViewById(R.id.other_player_score);

        tvUsername.setText(preview.getUsername());
        tvTotalScore.setText(preview.getScore().toString());

        return view;
    }
}
