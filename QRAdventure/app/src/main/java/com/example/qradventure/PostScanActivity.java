package com.example.qradventure;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Activity that comes immediately after scanning a QR code.
 * Allows the player to manage and interact with the code they have just scanned.
 */
public class PostScanActivity extends AppCompatActivity {
    private QR qr;
    private String recordID;
    private Button photoButton;
    private ActivityResultLauncher cameraLaunch;
    private Boolean keepImage = false;
    private Bitmap image;

    /**
     * Sets layout and gets QR Content from intent
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan2);
        setTitle("Post Scan Activity");
        keepImage = false;
        // unfold intent, create QR object.
        Intent intent = getIntent();
        String QRContent = intent.getStringExtra(getString(R.string.EXTRA_QR_CONTENT));
        qr = new QR(QRContent);



        cameraLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null){
                            Bundle bundle = result.getData().getExtras();

                            keepImage = true;

                            image = (Bitmap)bundle.get("data");

                            Log.d("IMAGE-SIZE:", Integer.toString(image.getByteCount()));

                            if (image.getByteCount() > (long)64000){
                                image = Bitmap.createScaledBitmap(image, 96 ,128, true);
                            }

                        }
                    }
                });



    }

    /**
     * Called when respective button is clicked.
     * Handles the logic of completing a QR code scan;
     * Updates firestore with the QR and creates a record.
     * @param view: unused
     */
    public void AddQR(View view) {
        try {
            Account myAccount = CurrentAccount.getAccount();

            // get firestore collection and desired document
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("QRDB").document(qr.getHash());

            // reference: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
            // Check for a document matching the qr hash
            QueryHandler query = new QueryHandler();

            query.addQR(qr, new Callback() {
                @Override
                public void callback(ArrayList<Object> args) {
                    Boolean exists = (Boolean)args.get(0);

                    if (exists){
                            // Document exists, therefore QR already in DB!
                            Context context = getApplicationContext();
                            CharSequence text = "That QR is in the DB!";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            // TODO: append CURRENT ACCOUNT to the list of players that have scanned this QR
                            //       use set(data, SetOptions.merge());


                    }else{
                            // Document does not exist, therefore this QR is brand new!
                            Context context = getApplicationContext();
                            CharSequence text = "New QR! Adding to Database...";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                    }

                }
            });


            //====== Create New Record ======//

            recordID = myAccount.getUsername() + "-" + qr.getHash();

            // Add the record to the current account
            Account currentAccount = CurrentAccount.getAccount();
            Record toAdd = new Record(currentAccount, qr);
            if (keepImage)
                toAdd.setImage(image);


            if (!currentAccount.containsRecord(toAdd)) {

                currentAccount.addRecord(toAdd);

                CurrentAccount.setAccount(currentAccount);

                // Add User to list of user scanned this qr
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("Username", myAccount.getUsername());
                docRef.collection("Scanned By").document(myAccount.getUsername()).set(userData);

                CollectionReference RecordDB = db.collection("RecordDB");

                QueryHandler addRecord = new QueryHandler();

                String androidDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                addRecord.addRecord(androidDeviceID, qr, myAccount, toAdd);

            }

            // ====== database logic concluded ======
            // send user to a different activity (which? Account for now?).
            Intent intent = new Intent(this, AccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            Log.d("logs", "Something went wrong");
        }
    }



    /**
     * Aborts the scan; does not add to account. Called when respective button is clicked.
     * @param view: unused
     */
    public void clickDismiss(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    /**
     * Records the geolocation of the scan. Called when respective button is clicked.
     * @param view: unused
     */
    public void addGeolocation(View view) {
        // To be developed further
    }

    /**
     * Sends to photo activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToPhoto(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null){
            cameraLaunch.launch(intent);
        }
        else{
            Toast.makeText(PostScanActivity.this, "Somthing went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends to ScannedBy activity. Called when respective button is clicked.
     * Uses a queryhandler to retrieve the needed information and a callback to
     * go to the desired activity
     * @param view: unused
     */
    public void goToScannedBy(View view) {
        QueryHandler q = new QueryHandler();

        q.getOthersScanned(qr.getHash(),new QueryCallback() {
            @Override
            public void callback(ArrayList<String> nameData, ArrayList<Long> scoreData) {

                Intent intent = new Intent(PostScanActivity.this, ScannedByActivity.class);

                intent.putExtra("NAMES", nameData);
                intent.putExtra("SCORES", scoreData);

                startActivity(intent);
            }
        });
    }

    /**
     * Sends to Comments activity. Called when respective button is clicked.
     * @param view: unused
     */
    public void goToComments(View view) {
        Intent intent = new Intent(this, CommentsActivity.class);

        intent.putExtra("QR Hash", qr.getHash());

        startActivity(intent);
    }

}