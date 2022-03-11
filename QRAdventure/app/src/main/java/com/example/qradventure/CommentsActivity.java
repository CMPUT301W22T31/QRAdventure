package com.example.qradventure;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Activity where anyone can view or add comments attached to a particular QR code.
 */
public class CommentsActivity extends AppCompatActivity {

    String recordID;
    int count = 0;
    ListView commentListView;
    ArrayAdapter<String> commentAdapter;
    ArrayList<String> commentArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setTitle("Player Comments");

        // Intent for record ID
        Intent intent = getIntent();
        recordID = intent.getStringExtra("Record ID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference recordRef = db.collection("RecordDB").document(recordID);

        // Update list of comments to display
        commentListView = findViewById(R.id.list_comments);

        commentAdapter = new ArrayAdapter<String>(this, R.layout.content_comment, commentArrayList);
        commentListView.setAdapter(commentAdapter);

        // Update number of comments
        TextView commentTitle = findViewById(R.id.text_comments_title);
        String commentTitleText = "Comments";
        commentTitle.setText(commentTitleText);

        EditText comment = findViewById(R.id.editText_comment);

        // Count the number of comments and add comments to ArrayList
        recordRef.collection("Comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String aComment = document.getData().get("Comment").toString();
                                commentArrayList.add(aComment);
                                count++;
                            }
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        Button addButton = findViewById(R.id.button_add_comment);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Add comment to Record collection
                HashMap<String, Object> CommentData = new HashMap<>();
                CommentData.put("Comment", comment.getText().toString());
                recordRef.collection("Comments").document(Integer.toString(count+1)).set(CommentData);
                count++;

                // Update number of comments
                String newTitle = "Comments (" + Integer.toString(count) + ")";
                commentTitle.setText(newTitle);

                commentArrayList.add(comment.getText().toString());
                commentAdapter.notifyDataSetChanged();
            }
        });

//        navbar = findViewById(R.id.navbar_menu);
//        navbar.setItemIconTintList(null);
//        navbar.setOnItemSelectedListener((item) ->  {
//            switch(item.getItemId()) {
//                case R.id.leaderboards:
//                    Log.d("check", "WORKING???");
//                    Intent intent1 = new Intent(getApplicationContext(), LeaderboardActivity.class);
//                    startActivity(intent1);
//                    break;
//                case R.id.search_players:
//                    Log.d("check", "YES WORKING???");
//                    Intent intent2 = new Intent(getApplicationContext(), SearchPlayersActivity.class);
//                    startActivity(intent2);
//                    break;
//                case R.id.scan:
//                    Intent intent3 = new Intent(getApplicationContext(), ScanActivity.class);
//                    startActivity(intent3);
//                    //goToScan();
//                    break;
//                case R.id.map:
//                    Intent intent5 = new Intent(getApplicationContext(), MapActivity.class);
//                    startActivity(intent5);
//                    break;
//                case R.id.my_account:
//                    // already on this activity. Do nothing.
//                    break;
//            }
//            return false;
//        });


    }
}