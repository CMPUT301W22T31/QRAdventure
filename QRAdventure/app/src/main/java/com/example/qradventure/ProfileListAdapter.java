package com.example.qradventure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProfileListAdapter extends ArrayAdapter<Integer> {
    Context context;
    private ArrayList<Integer> data;

    public ProfileListAdapter(Context context, ArrayList<Integer> data) {
        super(context, R.layout.single_qr, R.id.QR_title);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singleItem = convertView;
        // Finding a view is a fairly expensive task so we gotta make a seperate class
        // that holds each item
        ProfileListViewHolder holder = null;
        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//            switch(position) {
//                case 0:
//                    singleItem = layoutInflater.inflate(R.layout.single_qr, parent, false);
//                    break;
//                case 1:
//                    // code block
//                    break;
//                case 2:
//                    // code block
//                    break;
//                case 3:
//                    // code block
//                    break;
//                case 4:
//                    // code block
//                    break;
//                case 5:
//                    // code block
//                    break;
//                default:
//                    // code block
//            }

            singleItem = layoutInflater.inflate(R.layout.profile_choice, parent, false);
        }
        TextView header = singleItem.findViewById(R.id.profile_header);

        header.setText(Integer.toString(position));

            return singleItem;
    }
}
