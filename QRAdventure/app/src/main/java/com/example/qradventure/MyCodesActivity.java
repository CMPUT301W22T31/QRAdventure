package com.example.qradventure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 * Activity where logged in user can access and manage the codes they have scanned
 * NOTE: This is functionally different from ViewCodesActivity.
 */
public class MyCodesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_codes);
        setTitle("My Codes");


    }
}