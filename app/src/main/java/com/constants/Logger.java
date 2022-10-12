package com.constants;

import android.util.Log;

import com.messaging.logistic.BuildConfig;

//import androidx.multidex.BuildConfig;

public class Logger {

    public static final boolean isDebug = BuildConfig.DEBUG;

    public static void LogInfo(String TAG, String msg) {
        //Log.i("Logger", "isDebug: " + isDebug);
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void LogWarning(String TAG, String msg) {
       // Log.w("Logger", "isDebug: " + isDebug);
        if (isDebug) {
            Log.w(TAG, msg);
        }
    }

    public static void LogDebug(String TAG, String msg) {
       // Log.d("Logger", "isDebug: " + isDebug);
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void LogError(String TAG, String msg) {
       // Log.e("Logger", "isDebug: " + isDebug);
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void LogVerbose(String TAG, String msg) {
       // Log.v("Logger", "isDebug: " + isDebug);
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

}
