package com.als.logistic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.als.logistic.fragment.ObdDiagnoseFragment;
import com.constants.Logger;

public class LocPermissionActivity extends FragmentActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

    }




  /*  @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(UILApplication.getInstance().isNightModeEnabled()){
            this.setTheme(R.style.DarkTheme);
        } else {
            this.setTheme(R.style.LightTheme);
        }
        setContentView(R.layout.actionbar_eld);

        TextView EldTitleTV = (TextView)findViewById(R.id.EldTitleTV);
        EldTitleTV.setText(getString(R.string.location_per));

        RelativeLayout rightMenuBtn = (RelativeLayout)findViewById(R.id.rightMenuBtn);
        ImageView eldMenuBtn = (ImageView)findViewById(R.id.eldMenuBtn);
        eldMenuBtn.setImageResource(R.drawable.back_btn);

        eldMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
*/


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
               // Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
                checkNearByDevicesGranted();
            }else{
                finish();
            }
        }else if(requestCode == 3){
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ObdDiagnoseFragment.LocationPermissionCallBack = true;
            }
            finish();
        }

    }



    public  void checkNearByDevicesGranted() {
        if (Build.VERSION.SDK_INT > 30) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED) {
                Logger.LogVerbose("TAG","Permission is granted");
                ObdDiagnoseFragment.LocationPermissionCallBack = true;
                finish();
               // return true;
            } else {
                Logger.LogVerbose("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT}, 3);
                //return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            Logger.LogVerbose("TAG","Permission is granted");
            ObdDiagnoseFragment.LocationPermissionCallBack = true;
            finish();
           // return true;

        }

    }


}
