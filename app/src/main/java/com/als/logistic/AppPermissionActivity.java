package com.als.logistic;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adapter.logistic.PermissionAdapter;
import com.background.LocationProviderChangedReceiver;
import com.background.service.BleDataService;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.models.PermissionModel;

import java.util.ArrayList;
import java.util.List;

public class AppPermissionActivity extends FragmentActivity implements View.OnClickListener {

    final int LOCATION_REQUEST          = 101;
    final int STORAGE_REQUEST           = 102;
    final int NEARBY_DEVICES_REQUEST    = 103;
    int LocationRequestCount            = 0;
    boolean PermissionDenied        = false;
    boolean PermissionGranted       = true;

    String LocationP               = "Location";
    String LocationPreciseP        = "Location Precise";
    String StorageP                = "Storage";
    String BluetoothP              = "Bluetooth";
    String NotificationP            = "Notifications";

    String BlePerDesc              = "Allow to scan near by bluetooth OBD devices";
    String LocationApproxDesc      = "Allow to access your approximate location";
    String LocationPreciseDesc     = "Allow to use your accurate location";
    String StorageDesc             = "Allow to write driver logs in storage";
    String SNotifictaionDesc       = "Allow to send notifications";

    List<PermissionModel> permissionList = new ArrayList<>();
    ListView permissionListView;
    ImageView eldMenuBtn;
    RelativeLayout eldMenuLay, rightMenuBtn;
    TextView EldTitleTV;
    PermissionAdapter permissionAdapter;
    boolean isBleNearByScanCalled = false;
    Globally globally;
    Constants constants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(UILApplication.getInstance().isNightModeEnabled()){
            this.setTheme(R.style.DarkTheme);
        } else {
            this.setTheme(R.style.LightTheme);
        }

        setContentView(R.layout.activity_permission);

        globally = new Globally();
        constants = new Constants();

        permissionListView  = findViewById(R.id.permissionListView);

        eldMenuBtn      = findViewById(R.id.eldMenuBtn);
        eldMenuLay      = findViewById(R.id.eldMenuLay);
        rightMenuBtn    = findViewById(R.id.rightMenuBtn);
        EldTitleTV      = findViewById(R.id.EldTitleTV);

        eldMenuBtn.setImageResource(R.drawable.back_white);
        EldTitleTV.setText(getString(R.string.app_permissions));
        rightMenuBtn.setVisibility(View.INVISIBLE);


        permissionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PermissionModel model = permissionList.get(i);
                String PermissionType = model.getPermissionType();
                if(!model.IsPermissionGranted()) {
                    if (PermissionType.equals(LocationP) || PermissionType.equals(LocationPreciseP)) {
                        ActivityCompat.requestPermissions(AppPermissionActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                LOCATION_REQUEST);
                    }else if(PermissionType.equals(StorageP)){
                        if(LocationRequestCount < 3) {
                            ActivityCompat.requestPermissions(AppPermissionActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);
                        }else{
                            Globally.EldScreenToast(eldMenuBtn, getString(R.string.storage_per_revoked_change), getResources().getColor(R.color.colorVoilation));
                        }
                        LocationRequestCount++;
                    }else if(PermissionType.equals(BluetoothP)){
                        if(!isBleNearByScanCalled) {
                            ActivityCompat.requestPermissions(AppPermissionActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
                                    Manifest.permission.BLUETOOTH_CONNECT}, NEARBY_DEVICES_REQUEST);
                        }else{
                            Globally.EldScreenToast(EldTitleTV, getString(R.string.ble_per_revoked_sett), getResources().getColor(R.color.colorVoilation));
                        }
                        isBleNearByScanCalled = true;
                    }
                }

                if (PermissionType.equals(LocationP) || PermissionType.equals(LocationPreciseP)) {
                    if(!constants.CheckGpsStatus(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.loc_off_status), Toast.LENGTH_LONG).show();
                    }
                    permissionAdapter.notifyDataSetChanged();
                }else if(PermissionType.equals(BluetoothP)){
                    if(!globally.isBleEnabled(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.ble_off_status), Toast.LENGTH_LONG).show();
                    }

                    permissionAdapter.notifyDataSetChanged();
                }


            }
        });


        eldMenuLay.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver( bluetoothStatusReceiver, new IntentFilter(ConstantsKeys.BluetoothObserver));
        registerReceiver(gpsReceiver, makeFilter());

        checkPermissions();

    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        return filter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(bluetoothStatusReceiver);
        unregisterReceiver(gpsReceiver);

    }

    private BroadcastReceiver bluetoothStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

         // boolean isDeviceBleOn = intent.getBooleanExtra(ConstantsKeys.BluetoothStatus, false);
          checkPermissions();

        }
    };


    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // boolean isDeviceBleOn = intent.getBooleanExtra(ConstantsKeys.BluetoothStatus, false);

            if (intent != null && intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //Do your stuff on GPS status change
                Logger.LogDebug("Location", "Location status update " );
                checkPermissions();
            }

        }
    };


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.eldMenuLay:
                finish();
                break;
        }

    }

    private void checkPermissions(){

        permissionList = new ArrayList<>();

        PermissionModel model;
        if (Build.VERSION.SDK_INT >= 23) {
            int PreciseLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int ApproximateLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int ExternalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int BleScanPermission = checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN);

            if (PreciseLocation == PackageManager.PERMISSION_GRANTED ||
                    ApproximateLocation == PackageManager.PERMISSION_GRANTED) {

                if (PreciseLocation != PackageManager.PERMISSION_GRANTED) {
                    model = new PermissionModel(LocationP, LocationApproxDesc, PermissionGranted, Constants.LocationApproximate);
                    permissionList.add(model);

                    model = new PermissionModel(LocationPreciseP, LocationPreciseDesc, PermissionDenied, Constants.LocationPrecise);
                    permissionList.add(model);

                }else{
                    model = new PermissionModel(LocationP, LocationPreciseDesc, PermissionGranted, Constants.LocationPrecise);
                    permissionList.add(model);
                }
            }else{
                model = new PermissionModel(LocationP, LocationPreciseDesc, PermissionDenied, Constants.NoLocation);
                permissionList.add(model);
            }


            if (ExternalStorage == PackageManager.PERMISSION_GRANTED) {
                Logger.LogVerbose("TAG","Permission is granted");

                model = new PermissionModel(StorageP, StorageDesc, PermissionGranted, Constants.NoLocation);
                permissionList.add(model);

            } else {
                Logger.LogVerbose("TAG","Permission is revoked");
                model = new PermissionModel(StorageP, StorageDesc, PermissionDenied, Constants.NoLocation);
                permissionList.add(model);

                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);

            }


            if (BleScanPermission == PackageManager.PERMISSION_GRANTED) {
                Logger.LogVerbose("TAG","Permission is granted");
                model = new PermissionModel(BluetoothP, BlePerDesc, PermissionGranted, Constants.NoLocation);
                permissionList.add(model);

            } else {
                Logger.LogVerbose("TAG","Permission is revoked");
                model = new PermissionModel(BluetoothP, BlePerDesc, PermissionDenied, Constants.NoLocation);
                permissionList.add(model);

               // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
               //         Manifest.permission.BLUETOOTH_CONNECT}, NEARBY_DEVICES_REQUEST);

            }

            permissionAdapter = new PermissionAdapter(getApplicationContext(), permissionList);
            permissionListView.setAdapter(permissionAdapter);

          /*  checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                Log.e(TAG, "User accepted the notifications!")
                sendNotification(this)
            }

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                Logger.LogVerbose("TAG","Permission is granted");

            } else {
                Logger.LogVerbose("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT}, NEARBY_DEVICES_REQUEST);

            }*/


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0) {

            switch (requestCode) {
                case STORAGE_REQUEST:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Globally.EldScreenToast(EldTitleTV, getResources().getString(R.string.storage_per_granted),
                                UILApplication.getInstance().getThemeColor());

                        checkPermissions();
                    }
                    break;


                case NEARBY_DEVICES_REQUEST:

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Globally.EldScreenToast(EldTitleTV, getResources().getString(R.string.ble_scan_per_granted),
                                UILApplication.getInstance().getThemeColor());

                        checkPermissions();

                    }

                    break;

                case LOCATION_REQUEST:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Globally.EldScreenToast(EldTitleTV, getResources().getString(R.string.loc_per_granted),
                                UILApplication.getInstance().getThemeColor());

                        checkPermissions();

                    }
                    break;
            }
        }


    }



}
