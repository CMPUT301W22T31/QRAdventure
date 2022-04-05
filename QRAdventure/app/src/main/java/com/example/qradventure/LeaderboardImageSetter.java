package com.example.qradventure;

import android.widget.ImageView;
import android.widget.TextView;

public class LeaderboardImageSetter {
    public void setImages(TextView scoreView, TextView nameView, ImageView imageView, PlayerPreview playerPreview) {
        int index = playerPreview.getProfilePicIndex();
        nameView.setText(playerPreview.getUsername());
        scoreView.setText(playerPreview.getScore());
        switch (index) {
            case 0:
                imageView.setBackgroundResource(R.drawable.ic_turtle);
                break;
            case 1:
                imageView.setBackgroundResource(R.drawable.ic_fish);
                break;
            case 2:
                imageView.setBackgroundResource(R.drawable.ic_butterfly);
                break;
            case 3:
                imageView.setBackgroundResource(R.drawable.ic_ladybug);
                break;
            case 4:
                imageView.setBackgroundResource(R.drawable.ic_crocodile);
                break;
            case 5:
                imageView.setBackgroundResource(R.drawable.ic_duck);
                break;
        }
    }

}
