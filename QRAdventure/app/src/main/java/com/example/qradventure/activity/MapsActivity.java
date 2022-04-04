package com.example.qradventure.activity;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.qradventure.model.Callback;
import com.example.qradventure.model.CurrentAccount;
import com.example.qradventure.model.DistanceQRPair;
import com.example.qradventure.model.NearByQR;
import com.example.qradventure.utility.QRDistanceInfoAdapter;
import com.example.qradventure.model.QueryHandler;
import com.example.qradventure.R;
import com.example.qradventure.model.Account;
import com.example.qradventure.model.QR;
import com.example.qradventure.model.Record;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qradventure.databinding.ActivityMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    Dialog nearByQRDialogue;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Account account;
    ListView qrDistanceListView;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<DistanceQRPair> nearQRs = new ArrayList<DistanceQRPair>();
    ArrayList<String> nearByQRs = new ArrayList<String>();
    String content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get the account from the singleton
        account = CurrentAccount.getAccount();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //getLocation();


        BottomNavigationView navbar = findViewById(R.id.navbar_menu);
        navbar = findViewById(R.id.navbar_menu);
        navbar.setItemIconTintList(null);
        navbar.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
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
                    IntentIntegrator tempIntent = new IntentIntegrator(MapsActivity.this);
                    tempIntent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    tempIntent.setCameraId(0);
                    tempIntent.setOrientationLocked(false);
                    tempIntent.setPrompt("Scanning");
                    tempIntent.setBeepEnabled(true);
                    tempIntent.setBarcodeImageEnabled(true);
                    tempIntent.initiateScan();

                    break;
                case R.id.map:
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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    // styling google maps, by google:
    // https://developers.google.com/maps/documentation/android-sdk/styling#raw-resource
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng coords = new LatLng(account.getLocation().get(1), account.getLocation().get(0));
        mMap.addMarker(new MarkerOptions().position(coords).title(account.getUsername())).setIcon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_user_location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords,15f));

        QueryHandler query = new QueryHandler();

        try {
            query.getNearbyQRs(account.getLocation(), new Callback() {
                @Override
                public void callback(ArrayList<Object> args) {
                    if (args.size() > 0) {

                        ArrayList<NearByQR> nearByQRS =  (ArrayList<NearByQR>) args.get(0);
                        for (NearByQR qr: nearByQRS
                        ) {

                            LatLng loc = new LatLng((Double) qr.getLatitude(),(Double) qr.getLongitude());
                            if (qr.hasBeenScanned()) {
                                Log.d("bruh", "in yo ");
                                mMap.addMarker(new MarkerOptions().title("You've scanned this qr already.").position(loc)).setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_check));
                            }
                            else {
                                mMap.addMarker(new MarkerOptions().title(qr.getScore().toString() + "pts").position(loc)).setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_qr_location));
                            }

                            Log.d("hi", "latitude "+  qr.getLatitude());
                            Log.d("hi", "longitude "+ qr.getLatitude());

                        }

                        for (NearByQR qr: nearByQRS
                        )
                        {
                            if (!qr.hasBeenScanned()) {
                                if (qr.getDistance() >= 1000) {
                                    // the format would be something like '10pts,1.2km"
                                    nearByQRs.add(qr.getScore() + "pts," + Math.round(qr.getDistance() / 1000) + "km");
                                } else {
                                    nearByQRs.add(qr.getScore() + "pts," + Math.round(qr.getDistance()) + "m");
                                }
                            }
                        }
                    }
                }
            });
        }
        catch (Exception e) {
            Log.e("logs",  e.toString());
        }


    }


    public void nearByQRList(){
        nearByQRDialogue.setContentView(R.layout.nearby_qr_list_dialogue);
        nearByQRDialogue.show();
    }



    /**
     * This method is called whenever a QR code is scanned. Takes the user to PostScanActivity
     * This method is copied into every activity which we can clock the scannable button from
     *
     *
     * Citation for using the Scanning library
     * Website:https://androidapps-development-blogs.medium.com
     * link:https://androidapps-development-blogs.medium.com/qr-code-scanner-using-zxing-library-in-android-fe667862feb7
     * authir: Golap Gunjun Barman, https://androidapps-development-blogs.medium.com/
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (content == null){
            super.onActivityResult(requestCode, resultCode, data);
        }
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // get the QR contents, and send it to next activity

        if (content == null)
            content = result.getContents();

        if (account == null)
            account = CurrentAccount.getAccount();

        if (content.contains("QRSTATS-")) {
            Intent intent = new Intent(MapsActivity.this, StatsActivity.class);

            // extract the username from QR content, and add it to intentExtra
            String username = content.split("-")[1];
            intent.putExtra(getString(R.string.EXTRA_USERNAME), username);

            // start activity
            startActivity(intent);

        }
        else if (content.contains("QRLOGIN-")) {
            QueryHandler q = new QueryHandler();
            String deviceID = content.toString().split("-")[1];
            q.getLoginAccount(deviceID, new Callback() {
                Intent intent = new Intent(MapsActivity.this, AccountActivity.class);
                @Override
                public void callback(ArrayList<Object> args) {
                    DocumentReference docRef = db.collection("AccountDB").document(account.getUsername());
                    docRef.update("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }
            });
        }

        else if (content != null && !account.containsRecord(new Record(account, new QR(content)))) {
            Intent intent = new Intent(MapsActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);

        }else{
            String text = "You have already scanned that QR";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }

    // This is for setting up the custom icon for the marker
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth()
                ,vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void nearByQRList(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.nearby_qr_list_dialogue ,null);
        alertDialogBuilder.setView(dialogView);

        qrDistanceListView = dialogView.findViewById(R.id.qr_distance_list);
        QRDistanceInfoAdapter adapter = new QRDistanceInfoAdapter(this, nearByQRs);
        qrDistanceListView.setAdapter(adapter);
        nearByQRDialogue = alertDialogBuilder.create();
        nearByQRDialogue.show();
    }
}