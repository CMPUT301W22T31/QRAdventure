package com.example.qradventure;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

        // set special attributes for top 3 ranked players

        ImageView profilepic = view.findViewById(R.id.leaderboard_profile_pic);

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
