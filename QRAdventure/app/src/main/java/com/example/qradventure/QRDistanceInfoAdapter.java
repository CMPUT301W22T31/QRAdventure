package com.example.qradventure;

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

import java.util.ArrayList;

public class QRDistanceInfoAdapter extends ArrayAdapter<String> {
    private ArrayList<String> qrDistances;
    private Context context;

    public QRDistanceInfoAdapter(Context context, ArrayList<String> data) {
        super(context,0, data);
        this.qrDistances = data;
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
        String distance = qrDistances.get(position);


        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.qr_distance_info, parent, false);
        }
        TextView distanceText = view.findViewById(R.id.qr_distance);

        distanceText.setText(distance);

        return view;
    }

}
