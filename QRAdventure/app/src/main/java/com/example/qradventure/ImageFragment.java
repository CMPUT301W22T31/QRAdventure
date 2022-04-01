package com.example.qradventure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ImageFragment extends DialogFragment {


    imageListener listener;
    ImageView imageDisplay;
    Bitmap image;

    public interface imageListener{
        void keepImage(Boolean res);
    }


    public void onAttach(Context context){
        super.onAttach(context);

        if (context instanceof imageListener) {
            listener = (imageListener)context;

        }else{
            throw new RuntimeException(context.toString());
        }

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_confirm_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());



        imageDisplay = view.findViewById(R.id.taken_image);

        imageDisplay.setImageBitmap((Bitmap)getArguments().get("data"));

        return builder
                .setView(view)
                .setTitle("Keep Image?")
                .setNegativeButton("Deny", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try{
                            listener.keepImage(true);
                        }
                        catch (Exception e){

                        }



                    }



                }).create();

    }


}






