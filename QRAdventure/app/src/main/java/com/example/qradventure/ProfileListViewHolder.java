package com.example.qradventure;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Holder class for a ProfileListView
 */
public class ProfileListViewHolder {
    private ImageView itemImage; // may change if we want custom qr images for each qr

    ProfileListViewHolder(View v) {
        itemImage = v.findViewById(R.id.imageView);

        itemImage.setImageResource(R.drawable.qr_code2_black); // again change this if we want custom qr images. I've set it to a default image for now.
    }

}
