package com.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.als.logistic.AppPermissionActivity;
import com.constants.Constants;
import com.constants.Logger;

public class LocationProviderChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Intent pushIntent = new Intent(context, AppPermissionActivity.class);
            context.startService(pushIntent);
        }

    }

}
