package com.background.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.constants.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.messaging.logistic.Globally;

public class LocationListenerService extends Service  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long MIN_TIME_LOCATION_UPDATES = 1 * 1000;   // 1 sec
    public static LocationRequest locationRequest;
    public static GoogleApiClient mGoogleApiClient;
    protected LocationManager locationManager;
    Globally global;


    @Override
    public void onCreate() {
        super.onCreate();

        global = new Globally();


        createLocationRequest(MIN_TIME_LOCATION_UPDATES);

        // check availability of play services
        if (global.checkPlayServices(getApplicationContext())) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            requestLocationWithoutPlayServices();
        }



    }


    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

    }



    @SuppressLint("RestrictedApi")
    protected void createLocationRequest(long time) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(time);
        locationRequest.setFastestInterval(time);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    private void requestLocationWithoutPlayServices(){
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_LOCATION_UPDATES,
                    10, locationListenerGPS);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                Globally.LATITUDE = "" + loc.getLatitude();
                Globally.LONGITUDE = "" + loc.getLongitude();
                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);
            } else {
                Globally.LATITUDE = "";
                Globally.LONGITUDE = "";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if(SharedPref.IsLocReceivedFromObd(getApplicationContext()) == false) {
                Globally.LATITUDE = "" + location.getLatitude();
                Globally.LONGITUDE = "" + location.getLongitude();
                Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);
            }


            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {
                Log.e("Log", "--stop");
                stopForeground(true);
                stopSelf();

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(SharedPref.IsLocReceivedFromObd(getApplicationContext()) == false) {
            Globally.LATITUDE = "" + location.getLatitude();
            Globally.LONGITUDE = "" + location.getLongitude();
            Globally.LONGITUDE = Globally.CheckLongitudeWithCycle(Globally.LONGITUDE);
        }


        if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                !SharedPref.getPassword(getApplicationContext()).equals("")) {
            Log.e("Log", "--stop");
            stopForeground(true);
            stopSelf();

        }

    }
}
