package com.messaging.logistic;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.provider.Settings;
import android.view.Window;

public class BlankActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

           // boolean isEnabled = Settings.System.getInt(this.getApplicationContext().getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) == 1;

            // requestWindowFeature(Window.FEATURE_NO_TITLE);

           // if(isEnabled) {
               // setContentView(R.layout.blank_activity);
         //  }
        }catch (Exception e){}

    }


}
