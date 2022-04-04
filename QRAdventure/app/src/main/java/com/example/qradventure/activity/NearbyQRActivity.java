package com.example.qradventure.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qradventure.model.DistanceQRPair;
import com.example.qradventure.R;

import java.util.ArrayList;

public class NearbyQRActivity extends AppCompatActivity {

    ArrayList<DistanceQRPair> qrs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_qr);



    }
}
