package com.example.qradventure.utility;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qradventure.R;
import com.example.qradventure.model.PlayerPreview;

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
     * @param position - position in list
     * @param convertView -
     * @param parent - super
     * @return View - custom view to display
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        PlayerPreview preview = players.get(position);


        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.player_list_entry, parent, false);
        }

        /*
         * Citation
         *      Website: Stackoverflow
         *      Link: https://stackoverflow.com/a/10479144
         *      Author: Samir Mangroliya
         *      Purpose: Set special attributes to items in a listview
         */
        int rank = preview.getRank();
        ImageView trophy = view.findViewById(R.id.ivTrophy);

        if (rank == 1) {
            // color filter GOLD
            trophy.setColorFilter(Color.argb(255,255,193,7));
        } else if (rank == 2) {
            // color filter SILVER
            trophy.setColorFilter(Color.argb(255,170,170,170));
        } else if (rank == 3) {
            // color filter BRONZE
            trophy.setColorFilter(Color.argb(255,178,127,78));
        } else {
            // set alpha to 0 to hide trophy
            trophy.setColorFilter(Color.argb(0,178,127,78));
        }

        // get and set the textviews
        TextView tvUsername = view.findViewById(R.id.other_player_name);
        TextView tvTotalScore = view.findViewById(R.id.other_player_score);
        TextView tvRank = view.findViewById(R.id.tvRank);

        tvUsername.setText(preview.getUsername());
        tvTotalScore.setText(preview.getScore());
        String stringRank = ""+preview.getRank();
        tvRank.setText(stringRank);

        return view;
    }

}