package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Activity where anyone can view or add comments attached to a particular QR code.
 */
public class CommentsActivity extends AppCompatActivity {
    int commentCount = 0;
    ListView commentListView;
    ArrayAdapter<Comment> commentAdapter;
    ArrayList<Comment> commentArrayList = new ArrayList<Comment>();
    TextView commentTitle;
    BottomNavigationView navbar;

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

        // ====== Event listener (+ live updates!) ======
        docRefQR.collection("Comments")
                .orderBy("__name__")
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
                    docRefQR.collection("Comments").document(Integer.toString(commentCount + 1)).set(CommentData);
                }
            }
        });

        // ====== back button functionality ======
        FloatingActionButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent5);
                    break;
                case R.id.my_account:
                    Intent intent4 = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(intent4);
                    break;
            }
            return false;
        });
    }
}