package com.constants;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class GPSRequest implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

   //Context context;
    FragmentActivity fActivity;
  //  GpsLocationListener listener;

    public GPSRequest(FragmentActivity context) {//, GpsLocationListener locListener
        this.fActivity = context;
       // this.listener = locListener;
    }

    public void EnableGPSAutoMatically( ) { //final boolean isClicked, final int flag
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(fActivity)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    int statusCode = status.getStatusCode();
                    final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (statusCode) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.d("LocationSettingsStatus", "Success");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.d("LocationSettingsStatus","GPS is not on");
                            try {
                                status.startResolutionForResult(fActivity, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.d("LocationSettingsStatus","Setting change not allowed");

                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConnected( Bundle bundle) {
        Log.d("onConnected", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "Suspended");

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {
        Log.d("onConnectionFailed","Failed");
    }




}
