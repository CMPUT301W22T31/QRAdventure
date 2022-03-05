package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;


/**
 * Activity where the logged in player can manage their account
 */
public class AccountActivity extends AppCompatActivity {
    Account account;
    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account Activity");
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        // get the account from the singleton
        account = CurrentAccount.getInstance().getCurrentAccount();
        //navbar = new Navbar(this);
        // give info to textviews to display
        // TODO: Move these to onResume() in case of updated info.
        String username = account.getUsername();
        String email = account.getEmail();
        String phoneNumber = account.getPhoneNumber();
        TextView displayUsername = findViewById(R.id.user_username);
        displayUsername.setText(username);
        TextView displayEmail = findViewById(R.id.user_email);
        displayEmail.setText(email);
        TextView displayPhoneNumber = findViewById(R.id.user_phone_number);
        displayPhoneNumber.setText(phoneNumber);

        navbar.setOnItemSelectedListener((item) ->  {
            switch(item.getItemId()) {
                case R.id.leaderboards:
                    Log.d("check", "WORKING???");
                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.search_players:
                    Log.d("check", "YES WORKING???");
                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.scan:
                    Intent intent3 = new Intent(getApplicationContext(), ScanActivity.class);
                    startActivity(intent3);
                    break;
            }
            return false;
        });



    }

    /**
     * Sends to edit info activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToEditInfo(View view) {
        Intent intent = new Intent(this, EditInfoActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to StatusQR activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToStatusQR(View view) {
        Intent intent = new Intent(this, StatusQRActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to LoginQR activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToLoginQR(View view) {
        Intent intent = new Intent(this, LoginQRActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to MyStats activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToMyStats(View view) {
        Intent intent = new Intent(this, MyStatsActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to MyCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToMyCodes(View view) {
        Intent intent = new Intent(this, MyCodesActivity.class);
        startActivity(intent);
    }

}