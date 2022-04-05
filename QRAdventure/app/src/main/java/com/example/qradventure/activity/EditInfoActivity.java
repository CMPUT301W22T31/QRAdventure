package com.example.qradventure.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.qradventure.R;

/**
 * Activity where logged in user can edit their account information
 */
public class EditInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        setTitle("Edit Info Activity");

    }

}