package com.messaging.logistic;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.Window;

public class BlankActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.blank_activity);

    }


}
