package com.background.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

    private UpdateViewListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String state = extras.getString("extra");
        mListener.updateView(state);
    }


    public interface UpdateViewListener{
            public void updateView(String state);
    }
}
