package com.example.qradventure;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class QRListViewHolder {
    private TextView QRTitle;
    private TextView pts;
    private ImageView itemImage; // may change if we want custom qr images for each qr
    QRListViewHolder(View v)
    {
        itemImage = v.findViewById(R.id.imageView);
        QRTitle = v.findViewById(R.id.QR_title);
        pts = v.findViewById(R.id.QR_pts);

        itemImage.setImageResource(R.drawable.qr_code2_black); // again change this if we want custom qr images. I've set it to a default image for now.

    }

    public TextView getQRTitle() {
        return QRTitle;
    }

    public void setQRTitle(String text) {
        QRTitle.setText(text);
    }

    public TextView getPts() {
        return pts;
    }

    public void setPts(String text) {
        pts.setText(text);
    }
}
