package com.example.qradventure.utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qradventure.R;

import java.util.ArrayList;

public class QRDistanceInfoAdapter extends ArrayAdapter<String> {
    private ArrayList<String> qrInfo;
    private Context context;

    public QRDistanceInfoAdapter(Context context, ArrayList<String> data) {
        super(context,0, data);
        this.qrInfo = data; // has pts and distance
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

        String[] info = qrInfo.get(position).split(",");
        String pts = info[0];
        String distance = info[1];

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.qr_distance_info, parent, false);
        }

        TextView distanceText = view.findViewById(R.id.qr_distance);
        TextView pointsText = view.findViewById(R.id.qr_pt_info);

        distanceText.setText(distance);
        pointsText.setText(pts);

        return view;
    }

}
