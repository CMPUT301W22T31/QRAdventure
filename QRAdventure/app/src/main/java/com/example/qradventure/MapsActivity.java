package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qradventure.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.collect.Maps;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Button nearbyQRButton;
    Account account;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<DistanceQRPair> nearQRs = new ArrayList<DistanceQRPair>();


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

        nearbyQRButton = findViewById(R.id.near_qr_button);


        // displaying nearby QR's logic
        nearbyQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryHandler query = new QueryHandler();

                query.getNearbyQRs(account.getLocation(), new Callback() {
                    @Override
                    public void callback(ArrayList<Object> args) {
                        if (args.size() > 0) {
                            for (Object item : args
                            ) {
                                String detail = (String) item;
                                Log.d("logs", detail);
                            }
                        }

                    }
                });

            }
        });



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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("logs", account.getLocation().get(0).toString());
        Log.d("logs", account.getLocation().get(1).toString());

        // Add a marker in Sydney and move the camera
        LatLng coords = new LatLng(account.getLocation().get(1), account.getLocation().get(0));
        mMap.addMarker(new MarkerOptions().position(coords).title(account.getUsername()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords,15f));

    }
    /**
     * Activity is called when the camera scans a QR code. Processes the result and redirects to
     * PostScanActivity
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
            Intent intent = new Intent(MapsActivity.this, PostScanActivity.class);
            intent.putExtra(getString(R.string.EXTRA_QR_CONTENT), content);
            startActivity(intent);
        }
    }

}