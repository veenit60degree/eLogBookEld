package com.background.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.constants.APIs;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class UpdateDriverService  extends Service implements LocationListener {

    private Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    boolean isUpdating = false;
    int flag = 0;
    Location location;
    Notification notification;
    float distance;
    NotificationManager notifier;
    public double latitude;
    double longitude;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 500.0f;   // 500 meters
    private static final long MIN_TIME_BW_UPDATES = 960000;    //960000
    protected LocationManager locationManager;
    Location mylocation = new Location("");
    Location dest_location = new Location("");
    LocationListener mlocListener;
    int listSize = 0;
    SharedPreference sharedPreference;
    JSONArray locationJsonArray;
    JSONObject locationObj, jsonObj;
    String geoData = "";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        //  doCorrectStuffThatWritesToDisk();
        StrictMode.setThreadPolicy(old);


        sharedPreference = new SharedPreference();

        mTimer = new Timer();
        mTimer.schedule(timerTask, MIN_TIME_BW_UPDATES, MIN_TIME_BW_UPDATES );
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            context = this;
            Log.i("tag", "on start");
            mylocation = getLocation(context);

            //mlocListener = new MyLocationListener();

            Double msg = mylocation.getLatitude();
            Log.i("my long", msg.toString());

            Double dest_lat = intent.getDoubleExtra("lat", 0.0);
            Double dest_lon = intent.getDoubleExtra("lon", 0.0);
            //   Log.i("get lat", dest_lat.toString());
            //   Log.i("get lon", dest_lon.toString());

            this.dest_location.setLatitude(dest_lat);
            this.dest_location.setLongitude(dest_lon);
            Log.i("get lon", dest_lon.toString());



        } catch (Exception e) {
            e.printStackTrace();

        }

        //return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            Log.e("Log", "Running");
        }
    };

    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
            stopUsingGPS();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("com.messaging.logistic");
        intent.putExtra("location", "torestore");
        sendBroadcast(intent);
    }



    @Override
    public void onLocationChanged(Location location) {


        mylocation = getLocation(context);
        Log.i("Tag", "location changed");
        distance = mylocation.distanceTo(dest_location);
        Log.i("Tag", "" + distance);
        if (flag == 0) {


            try {
                geoData = "";
                locationJsonArray = new JSONArray();
                listSize = SharedPreference.loadSavedLocations(getApplicationContext()).size();
            } catch (Exception e) {
                e.printStackTrace();
                listSize = 0;
                Log.d("jsonStr", ">>>called Exception: " );
            }

            try {
                if(listSize > 0){

                    List<LocationModel> tempList = new ArrayList<LocationModel>();
                    tempList = SharedPreference.loadSavedLocations(getApplicationContext());

                    for(int i = 0; i < tempList.size() ; i++){

                        locationObj = new JSONObject();
                        locationObj.put("Latitude", tempList.get(i).getLatitude());
                        locationObj.put("Longitude", tempList.get(i).getLongitude());
                        locationObj.put("VehicleSpeed", tempList.get(i).getVehicleSpeed());
                        locationObj.put("TruckDateTime", tempList.get(i).getTruckDateTime());

                        locationJsonArray.put(locationObj);

                    }

                }


            } catch (Exception e) {
                listSize = 0;
            }




            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String currentDateTime = dateFormatter.format(today);

            LocationModel locationModel = new LocationModel(String.valueOf(mylocation.getLatitude()),
                    String.valueOf(mylocation.getLongitude()),
                    String.valueOf(mylocation.getSpeed()),
                    currentDateTime);

            sharedPreference.addDriverLocation(getApplicationContext(), locationModel );




            try {

                jsonObj = new JSONObject();
                jsonObj.put("Latitude",  String.valueOf(mylocation.getLatitude()) );
                jsonObj.put("Longitude", String.valueOf(mylocation.getLongitude()) );
                jsonObj.put("VehicleSpeed",String.valueOf(mylocation.getSpeed()) );
                jsonObj.put("TruckDateTime", currentDateTime);

                locationJsonArray.put(jsonObj);
                geoData = locationJsonArray.toString();

                if(Globally.isConnected(this)) {
                    if (!isUpdating) {
                        UPDATE_VEHICLE_POSITION(SharedPref.getDriverId( getApplicationContext())
                                , Globally.registrationId, geoData );
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


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


    @SuppressLint("MissingPermission")
    public Location getLocation(Context context) {

        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.i("No gps and No Network ",
                        "No gps and No Network is enabled enable either one of them");
                Toast.makeText(this, "Enable either Network or GPS",
                        Toast.LENGTH_LONG).show();
            } else {
                this.canGetLocation = true;

                checkLocationPermission(context);

                if (isNetworkEnabled) {



                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            //  Log.d("Network Latitude", ">>Network Latitude: " + latitude);
                            //   Log.d("Network longitude", ">>Network longitude: " + longitude);

                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                //   Log.d("GPS Latitude", ">>GPS Latitude: " + latitude);
                                //   Log.d("GPS longitude", ">>GPS longitude: " + longitude);

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }




    @SuppressLint("MissingPermission")
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(UpdateDriverService.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }


/*
	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {


		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	*/

    public boolean checkLocationPermission(final Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return false;
        } else {
            return true;
        }
    }




    void UPDATE_VEHICLE_POSITION(final String DriverId, final String DeviceId, final String geoData){

        RequestQueue queue = Volley.newRequestQueue(this);

        isUpdating = true;

        StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.VEHICLE_TRACKING,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        // response
                        Log.d("Response ", response);
                        isUpdating = false;
                        String status = "";

                        try {
                            Globally.obj = new JSONObject(response);
                            status = Globally.obj.getString("Status");
                            // message = Globally.obj.getString("Message");


                            if (status.equalsIgnoreCase("true")) {
                                SharedPreference.clearLocationFromList(getApplicationContext());

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // response
                        Log.d("error", "error " +error);
                        isUpdating = false;

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                 params.put(ConstantsKeys.DeviceId, DeviceId );
                params.put(ConstantsKeys.DriverId, DriverId );
                params.put(ConstantsKeys.GeoData, geoData);

               /* params.put("Lanttitude", Lanttitude);
                 params.put(ConstantsKeys.Longitude, Longitude );
                params.put("VehicleId", VehicleId );
                params.put("VehicleSpeed", VehicleSpeed );*/


                return params;
            }
        };
        queue.add(postRequest);




    }



}



