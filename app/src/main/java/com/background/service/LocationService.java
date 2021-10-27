package com.background.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.constants.SharedPref;
import com.messaging.logistic.Globally;


/**
 * Created by kumar on 1/6/2017.
 */

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int LOC_DURATION = 3000  ;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    //int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        initilizeLocationManager();
    }

    void initilizeLocationManager(){
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOC_DURATION, 5, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_DURATION, 5, listener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                !SharedPref.getPassword(getApplicationContext()).equals("")) {
            Log.e("Log", "--stop");
            stopForeground(true);
            stopSelf();

        }else{
            if(locationManager == null){
                initilizeLocationManager();
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > LOC_DURATION;
        boolean isSignificantlyOlder = timeDelta < -LOC_DURATION;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(locationManager != null && listener != null) {
            locationManager.removeUpdates(listener);
            locationManager = null;
            listener = null;
        }

    }





    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("onLocationChanged", "Location: " + loc.getLatitude());
            Globally.LATITUDE = "" + loc.getLatitude();
            Globally.LONGITUDE = "" + loc.getLongitude();

            if (!SharedPref.getUserName(getApplicationContext()).equals("") &&
                    !SharedPref.getPassword(getApplicationContext()).equals("")) {
                Log.e("Log", "--stop");
                stopForeground(true);
                stopSelf();

            }

        }

        public void onProviderDisabled(String provider)  {
            Globally.LATITUDE = "" ;
            Globally.LONGITUDE = "" ;

            Log.d("onProviderDisabled", "Gps Disabled" );
        }


        public void onProviderEnabled(String provider)  {
            Log.d("onProviderEnabled", "Gps Enabled" );
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {  }

    }
}