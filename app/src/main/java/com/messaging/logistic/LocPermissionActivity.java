package com.messaging.logistic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.messaging.logistic.fragment.ObdDiagnoseFragment;

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
                Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
                ObdDiagnoseFragment.LocationPermissionCallBack = true;
            }

            finish();
        }

    }




}
