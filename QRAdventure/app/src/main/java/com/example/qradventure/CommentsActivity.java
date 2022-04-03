package com.example.qradventure;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Activity where anyone can view or add comments attached to a particular QR code.
 */
public class CommentsActivity extends AppCompatActivity {
    Account account;
    int commentCount = 0;
    ListView commentListView;
    ArrayAdapter<Comment> commentAdapter;
    ArrayList<Comment> commentArrayList = new ArrayList<Comment>();
    TextView commentTitle;
    BottomNavigationView navbar;
    FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Initialize onClick and onEvent listeners
     * Listeners contains query logic to get and set comments in database
     * Enables navbar
     * @param savedInstanceState - Unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Account & Intent for QR Hash
        Account myAccount = CurrentAccount.getAccount();
        Intent intent = getIntent();
        String hash = intent.getStringExtra("QR Hash");

        // Get firestore references
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefQR = db.collection("QRDB").document(hash);

        // Initialize display references
        commentListView = findViewById(R.id.list_comments);
        commentAdapter = new CommentList(this, commentArrayList);
        commentListView.setAdapter(commentAdapter);
        commentTitle = findViewById(R.id.text_comments_title);


        // Update number of comments
        TextView commentTitle = findViewById(R.id.text_comments_title);
        String commentTitleText = "Comments";
        commentTitle.setText(commentTitleText);

        EditText enteredComment = findViewById(R.id.editText_comment);
        // Call FusedLocationProviderClient class to grab location of user
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // ====== Event listener (+ live updates!) ======
        docRefQR.collection("Comments")
                .orderBy("Position", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException error) {
                        // clear the old list
                        commentArrayList.clear();

                        // get the documents
                        for (QueryDocumentSnapshot commentDoc : queryDocumentSnapshots) {
                            String author = (String) commentDoc.get("Author");
                            String text = (String) commentDoc.get("Comment");
                            Comment newComment = new Comment(author, text);
                            commentArrayList.add(newComment);
                        }


                        // notify data set changed
                        commentAdapter.notifyDataSetChanged();

                        // update count & title
                        commentCount = queryDocumentSnapshots.size();
                        String newTitle = "Comments (" + commentCount + ")";
                        commentTitle.setText(newTitle);
                    }
        });

        // ====== Logic to add a comment ======
        EditText etComment = findViewById(R.id.editText_comment);
        Button addButton = findViewById(R.id.button_add_comment);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // get and clear input
                String inputText = etComment.getText().toString();
                etComment.setText("");

                if (inputText.length() > 0) {

                    // Add comment to QR Comments collection
                    HashMap<String, Object> CommentData = new HashMap<>();
                    CommentData.put("Comment", inputText);
                    CommentData.put("Author", myAccount.getUsername());
                    CommentData.put("Position", commentCount);
                    docRefQR.collection("Comments").document(Integer.toString(commentCount)).set(CommentData);
                }
            }
        });


        // ====== Navbar functionality ======
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
                    IntentIntegrator tempIntent = new IntentIntegrator(CommentsActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();
                    break;
                case R.id.map:
                    if (ActivityCompat.checkSelfPermission(CommentsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // grab location of user before map activity starts
                        try {

                            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
                            locationGrabber.getLocation(this);
                            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent5);
                        }
                        catch (Exception e){
                            Log.d("logs", e.toString());
                        }
                    }
                    else {
                        ActivityCompat.requestPermissions(CommentsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
            }
            return false;
        });
    }
    /**
     * Grabs location of user before entering maps activity
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        if (requestCode == 44) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.d("logs", "Grabbing location ");
            Log.d("logs", "Location before: " + account.getLocation().toString() );
            LocationGrabber locationGrabber = new LocationGrabber(fusedLocationProviderClient);
            locationGrabber.getLocation(this);
            Intent intent5 = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent5);
            Log.d("logs", "Location after: " + account.getLocation().toString() );
        }
    }
    /**
     * This method is called whenever a QR code is scanned. Takes the user to PostScanActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity
        String content = result.getContents();
        Account account = CurrentAccount.getAccount();

        if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
            Intent intent = new Intent(CommentsActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);

        }else{
            String text = "You have already scanned that QR";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }


}