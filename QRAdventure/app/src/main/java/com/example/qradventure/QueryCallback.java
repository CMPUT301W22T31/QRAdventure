package com.example.qradventure;

import java.util.ArrayList;
import android.content.Intent;

import androidx.core.util.Pair;

public interface QueryCallback {

    void callback(ArrayList<String> nameData, ArrayList<Long> scoreData );


}
