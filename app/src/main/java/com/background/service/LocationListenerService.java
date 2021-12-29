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

import com.constants.Constants;
import com.constants.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.messaging.logistic.Globally;

public class LocationListenerService extends Service  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long MIN_TIME_LOCATION_UPDATES = 3 * 1000;   // 1 sec
    public static LocationRequest locationRequest;
    public static GoogleApiClient mGoogleApiClient;
    protected LocationManager locationManager;
    Globally global;
    Constants constants;

    @Override
    public void onCreate() {
        super.onCreate();

        global = new Globally();
        constants = new Constants();

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

            Globally.GPS_LATITUDE = "" + location.getLatitude();
            Globally.GPS_LONGITUDE = "" + location.getLongitude();

            if(SharedPref.IsLocReceivedFromObd(getApplicationContext()) == false) {

                Globally.LATITUDE = Globally.GPS_LATITUDE;
                Globally.LONGITUDE = Globally.GPS_LONGITUDE;

                // saving location with time info to calculate location malfunction event
                  constants.saveEcmLocationWithTime(Globally.GPS_LATITUDE, Globally.GPS_LONGITUDE,
                          SharedPref.getHighPrecisionOdometer(getApplicationContext()), getApplicationContext());

            }


            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {
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
        //  Log.d("onConnected", "onConnected");
        try {
            requestLocationUpdates();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {

        Globally.GPS_LATITUDE = "" + location.getLatitude();
        Globally.GPS_LONGITUDE = "" + location.getLongitude();

        if(SharedPref.IsLocReceivedFromObd(getApplicationContext()) == false) {

            Globally.LATITUDE = Globally.GPS_LATITUDE;
            Globally.LONGITUDE = Globally.GPS_LONGITUDE;

            // saving location with time info to calculate location malfunction event
            constants.saveEcmLocationWithTime(Globally.GPS_LATITUDE, Globally.GPS_LONGITUDE,
                    SharedPref.getHighPrecisionOdometer(getApplicationContext()), getApplicationContext());

        }


        if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                !SharedPref.getPassword(getApplicationContext()).equals("")) {
            stopForeground(true);
            stopSelf();

        }

    }


    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, (LocationListener) this);
    }


    protected void StopLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected()) {
                stopForeground(true);
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
                mGoogleApiClient.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }




    @Override
    public void onDestroy() {

        try {
            StopLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();


    }
}
