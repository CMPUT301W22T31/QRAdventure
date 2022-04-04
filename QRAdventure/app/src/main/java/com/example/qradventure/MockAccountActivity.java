package com.example.qradventure;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MockAccountActivity extends AccountActivity {

    String testQR;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testQR = getIntent().getStringExtra("TEST");


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        content = testQR;
        super.onActivityResult(requestCode, resultCode, data);


    }
}
