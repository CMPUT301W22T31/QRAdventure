package com.example.qradventure;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qradventure.databinding.ActivityAddGeolocationBinding;

public class AddGeolocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private ActivityAddGeolocationBinding binding;
    private Marker marker;
    Double longitude;
    Double latitude;
    Account account;
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddGeolocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the account from the singleton
        account = CurrentAccount.getAccount();

        confirmBtn = findViewById(R.id.confirm_geo);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                setResult(RESULT_OK,intent);
                Toast.makeText(AddGeolocationActivity.this, "Recorded geolocation!", Toast.LENGTH_SHORT).show();
                finish();
            }
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

        // Add a marker in Sydney and move the camera
        LatLng coords = new LatLng(account.getLocation().get(1), account.getLocation().get(0));

        // Initialize longitude/latitude as what the current location of account is
        longitude = account.getLocation().get(0);
        latitude = account.getLocation().get(1);

        Log.d("logs", "Add geolocation longitude: " + longitude.toString());
        Log.d("logs", "Add geolocation latitude: " + latitude.toString());
        mMap.addMarker(new MarkerOptions().position(coords)
                .title(account.getUsername())
                .draggable(true))
                .setIcon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_qr_location));

        mMap.setOnMarkerDragListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15f));
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        Toast.makeText(this, "Lat" + marker.getPosition().latitude + " lng " + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
        longitude = marker.getPosition().longitude;
        latitude = marker.getPosition().latitude;
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

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
}