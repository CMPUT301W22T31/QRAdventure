package com.example.qradventure;

import java.util.ArrayList;
import android.content.Intent;


import androidx.core.util.Pair;


/**
 * Special callback since we were having trouble passing two ArrayLists as args in the other one
 */
public interface QueryCallback {

    void callback(ArrayList<String> nameData, ArrayList<Long> scoreData );

}
