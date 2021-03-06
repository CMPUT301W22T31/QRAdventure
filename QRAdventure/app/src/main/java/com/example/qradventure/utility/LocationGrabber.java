package com.example.qradventure.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qradventure.model.Account;
import com.example.qradventure.model.CurrentAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Used to get the location of the current user
 * Uses CurrentAccount singleton to get the account, and set its location
 */
public class LocationGrabber {
    Account account;
    FusedLocationProviderClient fusedLocationProviderClient;

    public LocationGrabber(FusedLocationProviderClient  fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        account = CurrentAccount.getAccount();
    }

    /**
     * Gets and sets location of current user
     * @param context
     */
    @SuppressLint("MissingPermission")
    public void getLocation(Context context) {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null) {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        ArrayList<Double> userLocation = new ArrayList<Double>();
                        userLocation.add(addresses.get(0).getLongitude());
                        userLocation.add(addresses.get(0).getLatitude());
                        account.setLocation(userLocation);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("logs", "location is null!");
                }

            }
        });
    }
}
