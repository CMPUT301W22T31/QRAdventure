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
     * Allows for trophies to be displayed on top ranked players
     * @param position - position in list
     * @param convertView - unused view to reuse as list scrolls
     * @param parent - ViewGroup
     * @return View - custom view to display within the listView
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        PlayerPreview preview = players.get(position);


        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.player_list_entry, parent, false);
        }

        // set special attributes for top 3 ranked players

        ImageView profilepic = view.findViewById(R.id.leaderboard_profile_pic);
        /*
         * Citation
         *      Website: Stackoverflow
         *      Link: https://stackoverflow.com/a/10479144
         *      Author: Samir Mangroliya
         *      Purpose: Set special attributes to items in a listview
         */
        int rank = preview.getRank();
        int profilePicIndex = preview.getProfilePicIndex();

        switch (profilePicIndex) {
            case 0:
                profilepic.setBackgroundResource(R.drawable.ic_turtle);
                //profileCard.setCardBackgroundColor(Color.parseColor("#4361EE"));
                break;
            case 1:
                profilepic.setBackgroundResource(R.drawable.ic_fish);
                //profileCard.setCardBackgroundColor(Color.parseColor("#3A0CA3"));
                break;
            case 2:
                profilepic.setBackgroundResource(R.drawable.ic_butterfly);
                //profileCard.setCardBackgroundColor(Color.parseColor("#a8dadc"));
                break;
            case 3:
                profilepic.setBackgroundResource(R.drawable.ic_ladybug);
                //profileCard.setCardBackgroundColor(Color.parseColor("#b5179e"));
                break;
            case 4:
                profilepic.setBackgroundResource(R.drawable.ic_crocodile);
                //profileCard.setCardBackgroundColor(Color.parseColor("#457b9d"));
                break;
            case 5:
                profilepic.setBackgroundResource(R.drawable.ic_duck);
                //profileCard.setCardBackgroundColor(Color.parseColor("#2a9d8f"));
                break;
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
