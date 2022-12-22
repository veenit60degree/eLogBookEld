package com.als.logistic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.custom.dialogs.UpdateAppDialog;

/**
 * Created by kumar on 11/30/2017.
 */

public class UpdateAppActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.blank_activity);

        UpdateAppDialog dialog = new UpdateAppDialog(this, this);
        dialog.show();

    }



}
