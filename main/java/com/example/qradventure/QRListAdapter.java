package com.example.qradventure;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


// Grid View adapter for the grid view in "MyCodesActivity" Class
public class QRListAdapter extends ArrayAdapter<Record>{
    Context context;
    private ArrayList<Record> accountRecords;


    public QRListAdapter(Context context, ArrayList<Record> accountRecords) {
        super(context, R.layout.single_qr, R.id.QR_title, accountRecords);
        this.context = context;
        this.accountRecords = accountRecords;
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
        holder.setQRTitle(accountRecords.get(position).getQRHash().substring(0,4));
        holder.setPts(Integer.toString(accountRecords.get(position).getQRscore()));

        return singleItem;
    }
}