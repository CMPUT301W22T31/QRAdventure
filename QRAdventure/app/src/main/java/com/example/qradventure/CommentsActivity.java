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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Activity where anyone can view or add comments attached to a particular QR code.
 */
public class CommentsActivity extends AppCompatActivity {

    String hash;
    int count = 0;
    ListView commentListView;
    ArrayAdapter commentAdapter;
    ArrayList<Comment> commentArrayList = new ArrayList<Comment>();
    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setTitle("Player Comments");

        // Intent for record ID
        Intent intent = getIntent();
        hash = intent.getStringExtra("QR Hash");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference QRRef = db.collection("QRDB").document(hash);

        // Update list of comments to display
        commentListView = findViewById(R.id.list_comments);

        commentAdapter = new CommentList(this, commentArrayList);
        commentListView.setAdapter(commentAdapter);

        // Update number of comments
        TextView commentTitle = findViewById(R.id.text_comments_title);
        String commentTitleText = "Comments";
        commentTitle.setText(commentTitleText);

        EditText enteredComment = findViewById(R.id.editText_comment);

        QueryHandler query = new QueryHandler();

        query.getComments(hash, new Callback() {
            @Override
            public void callback(ArrayList<Object> args) {

                for (Object o: args){
                    commentArrayList.add( (Comment)o);
                    count++;
                }
                commentAdapter.notifyDataSetChanged();
            }
        });


        FloatingActionButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QRPageActivity.class);
                intent.putExtra("QRtitle", hash.substring(0,4));
                intent.putExtra("QRHash", hash);
                startActivity(intent);
            }
        });

        Button addButton = findViewById(R.id.button_add_comment);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Account myAccount = CurrentAccount.getAccount();

                // Add comment to Record collection
                HashMap<String, Object> CommentData = new HashMap<>();
                CommentData.put("Comment", enteredComment.getText().toString());
                CommentData.put("Author", myAccount.getUsername());
                QRRef.collection("Comments").document(myAccount.getUsername() + Integer.toString(count+1)).set(CommentData);
                count++;

                // Update number of comments
                String newTitle = "Comments (" + Integer.toString(count) + ")";
                commentTitle.setText(newTitle);

                Comment commentObject = new Comment(myAccount.getUsername(), enteredComment.getText().toString());

                commentArrayList.add(commentObject);
                commentAdapter.notifyDataSetChanged();

                enteredComment.setText("");
            }
        });

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
        if (content != null) {
            Intent intent = new Intent(CommentsActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        }
    }


}