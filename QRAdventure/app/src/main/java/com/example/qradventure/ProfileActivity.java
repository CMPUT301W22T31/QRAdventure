package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;


/**
 * Activity displaying the profile of any player. Anyone can access this activity.
 */
public class ProfileActivity extends AppCompatActivity {
    private String username;
    BottomNavigationView navbar;
    FirebaseFirestore db;

    /**
     * Gets and displays fields of a player's profile
     * Enables navbar
     * @param savedInstanceState - unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();

        Button deleteButton = findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.INVISIBLE);

        // unpack intent to get account username
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.EXTRA_USERNAME));

        // display delete button if owner
        if (intent.getStringExtra("Owner") == "Owner") {
            deleteButton.setVisibility(View.VISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(username);
            }
        });

        // ====== Enable navbar functionality ======
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
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
                    // Use IntentIntegrator to activate camera
                    IntentIntegrator tempIntent = new IntentIntegrator(ProfileActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
                case R.id.map:
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        });

        // ====== query DB for display fields ======
        QueryHandler query = new QueryHandler();
        query.getProfile(username, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {
                String email = (String) args.get(0);
                String phone = (String) args.get(1);
                Long totalScore = (Long)args.get(2);
                setTextViews(username, email, phone, totalScore.toString());
            }
        });
    }

    /**
     * Sends to ViewCodes activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToViewCodes(View view) {
        Intent intent = new Intent(this, ViewCodesActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USERNAME), username);
        startActivity(intent);
    }

    /**
     * Updates the textviews to display user data
     * @param name - account username
     * @param email - account email
     * @param phone - account phone #
     * @param score - account score
     */
    public void setTextViews(String name, String email, String phone, String score) {
        // get reference to the textviews
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvTotalScore = findViewById(R.id.tvTotalScore);

        // set textview text
        tvUsername.setText(name);
        tvPhone.setText(email);
        tvEmail.setText(phone);
        tvTotalScore.setText(score);
    }

    /**
     * Delete this account. Called when delete button is clicked. Owner functionality.
     * @param username - account username to delete
     */
    public void deleteAccount(String username) {
        db.collection("AccountDB").document(username)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Context context = getApplicationContext();
                        CharSequence text = "Account successfully deleted";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Context context = getApplicationContext();
                        CharSequence text = "Error deleting account";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();                    }
                });
    }
}