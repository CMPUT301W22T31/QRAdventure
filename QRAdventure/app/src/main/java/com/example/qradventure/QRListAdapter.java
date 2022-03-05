package com.example.qradventure;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


// Grid View adapter for the grid view in "MyCodesActivity" Class
public class QRListAdapter extends ArrayAdapter<String>{
    Context context;
    String[] qrName;
    String[] pts;

    public QRListAdapter(Context context, String[] qrName, String[] pts) {
        super(context, R.layout.single_qr, R.id.QR_title, qrName);
        this.context = context;
        this.qrName = qrName;
        this.pts = pts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singleItem = convertView;
        // Finding a view is a fairly expensive task so we gotta make a seperate class
        // that holds each item
        QRListViewHolder holder = null;
        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_qr, parent, false);
            holder = new QRListViewHolder(singleItem);
            singleItem.setTag(holder);
        }
        else {
            holder = (QRListViewHolder) singleItem.getTag();
        }
        holder.setQRTitle(qrName[position]);
        holder.setPts(pts[position]);

        return singleItem;
    }
}
