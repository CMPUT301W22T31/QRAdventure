package com.example.qradventure;

import java.util.ArrayList;
import android.content.Intent;


import androidx.core.util.Pair;


/**
 *
 * WILL SOON BE REPLACED WITH THE REGULAR CALLBACK IN PART 4
 */
public interface QueryCallback {

    void callback(ArrayList<String> nameData, ArrayList<Long> scoreData );

}
