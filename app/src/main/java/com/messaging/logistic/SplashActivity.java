package com.messaging.logistic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.background.service.AfterLogoutService;
import com.background.service.BackgroundLocationService;
import com.constants.Constants;
import com.constants.SharedPref;
import com.custom.dialogs.ConfirmationDialog;
import com.custom.dialogs.PermissionInfoDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.wifi.settings.WiFiConfig;

public class SplashActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    String ScreenName = "";
    TextView appVersionSplash;
    boolean isFirst = false;
    Handler handler;
    PermissionInfoDialog permissionInfoDialog;


    protected static final String TAG = "SplashActivity";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    /**
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    boolean IsTablet = false;
    Globally global;
    Constants constants;
    protected LocationManager locationManager;
    RelativeLayout splashLay, splashMainLay;
    final int LOCATION_REQUEST  = 101;
    final int STORAGE_REQUEST   = 102;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_activity);

        constants = new Constants();
        global = new Globally();
        handler = new Handler();


            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();

        Constants.IsAlsServerResponding = true;
        Constants.IsHomePageOnCreate = true;

        // check availability of play services
        if (!global.checkPlayServices(getApplicationContext())) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        splashLay           = (RelativeLayout)findViewById(R.id.splashLay);
        splashMainLay       = (RelativeLayout)findViewById(R.id.splashMainLay);
        appVersionSplash    = (TextView)findViewById(R.id.appVersionSplash);
        appVersionSplash.setText("Version " + Globally.GetAppVersion(this, "VersionName"));

        IsTablet    = Globally.isTablet(this);

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            splashMainLay.setBackgroundColor(getResources().getColor(R.color.gray_background));
            splashLay.setBackgroundColor(getResources().getColor(R.color.gray_background));
        }

        SharedPref.setLoginAllowedStatus(true, getApplicationContext());
        SharedPref.setLastCalledWiredCallBack(0, getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isFirst){
            handler.postDelayed(new Runnable() {
                public void run() {
                    CheckPermissionViewStatus();
                }
            }, 2000);
        }else{
            CheckPermissionViewStatus();
        }

        isFirst = true;

    }


    private boolean requestLocationPermission(){


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                statusCheck();

                return true;
            } else {
               // Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST);
                return false;
            }
        }else { //permission is automatically granted on sdk<23 upon installation
           // Log.v("TAG","Permission is granted");
            statusCheck();
            return true;
        }

    }



    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                statusCheck();

                return true;
            } else {
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            statusCheck();
            return true;
        }

    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (global.checkPlayServices(getApplicationContext())) {
                checkLocationSettings();
            }else{
                requestLocationWithoutPlayServices();
            }
        } else {
            CheckUserCredientials();
        }
    }


    protected synchronized void buildGoogleApiClient() {
       // Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }


    private void requestLocationWithoutPlayServices(){


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                askGpsTurnOn();

            } else {
              //  Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 4);
            }
        }else { //permission is automatically granted on sdk<23 upon installation
          //  Log.v("TAG","Permission is granted");
            askGpsTurnOn();
        }

    }

    void askGpsTurnOn(){
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        boolean isGps = constants.CheckGpsStatus(SplashActivity.this);
        if(isGps){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    UPDATE_INTERVAL_IN_MILLISECONDS,
                    10, locationListenerGPS);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null){
                global.LATITUDE = "" +loc.getLatitude();
                global.LONGITUDE = "" +loc.getLongitude();
                global.LONGITUDE = Globally.CheckLongitudeWithCycle(global.LONGITUDE);
            }

            CheckUserCredientials();
        }else{
            Toast.makeText(getApplicationContext(), "Turn on your network location first", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
           // CheckUserCredientials();
        }
    }


    void CheckUserCredientials(){
        if (!SharedPref.getUserName( SplashActivity.this).equals("") &&
                !SharedPref.getPassword( SplashActivity.this).equals("")) {

            ScreenName = "home";
            MoveToNextScreen(ScreenName);
        } else {
            ScreenName = "login";
            MoveToNextScreen(ScreenName);
        }
    }



    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            global.LATITUDE = "" +location.getLatitude();
            global.LONGITUDE = "" +location.getLongitude();
            global.LONGITUDE = Globally.CheckLongitudeWithCycle(global.LONGITUDE);
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



    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
               // Log.i(TAG, "All location settings are satisfied.");
                // startLocationUpdates();

                CheckUserCredientials();

                break;


            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }

                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                CheckUserCredientials();

                break;

                default:
                    Log.i(TAG, "default ");
                    CheckUserCredientials();

                    break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        //startLocationUpdates();

                        CheckUserCredientials();

                        break;

                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }





    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }



    void checkUserStatus(){
        if (!SharedPref.getUserName(SplashActivity.this).equals("") &&
                !SharedPref.getPassword(SplashActivity.this).equals("")) {
            // =============== Check storage permission =====================
            if (Build.VERSION.SDK_INT < 23) {
                statusCheck();
            } else {
                isStoragePermissionGranted();
            }
        } else {
            if (global.checkPlayServices(getApplicationContext())) {
                requestLocationPermission();
            } else {
                requestLocationWithoutPlayServices();
            }
        }
    }


    void CheckPermissionViewStatus(){

        if(SharedPref.getPermissionInfoViewStatus(this) == false){
            try {
                if (permissionInfoDialog != null && permissionInfoDialog.isShowing()) {
                   Log.d("permissionInfoDialog", "already showing");
                }else{
                    permissionInfoDialog = new PermissionInfoDialog(SplashActivity.this, new TermsAgreeListener() );
                    permissionInfoDialog.show();
                }


            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }else {
            checkUserStatus();
        }
    }



    /*================== Confirmation Listener ====================*/
    private class TermsAgreeListener implements PermissionInfoDialog.TermsAgreeListener {


        @Override
        public void AgreeReady() {
            // set view status true to avoid show this window next time
            SharedPref.SetPermissionInfoViewStatus(true, SplashActivity.this);
            checkUserStatus();

            permissionInfoDialog.dismiss();

        }
    }



    void MoveToNextScreen(String screen){

       // constants.checkBleConnection();

        /*========= Call main Service to start obd server service =============*/
        Constants.isEldHome = false;
        Intent intent;

        if(screen.equals("home")) {
            intent = new Intent(SplashActivity.this, TabAct.class);
        }else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        // call home/login activity
        intent.putExtra("user_type", "splash");
        startActivity(intent);
        finish();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {

            case STORAGE_REQUEST:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    statusCheck();

                }else{

                }
                break;


            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    statusCheck();
                }else{
                    CheckUserCredientials();
                }
                break;
        }

    }


    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
        Log.d("location", "location " + location);
    }



}
