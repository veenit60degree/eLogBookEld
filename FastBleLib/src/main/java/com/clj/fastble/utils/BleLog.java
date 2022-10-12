package com.clj.fastble.utils;


import android.util.Log;

public final class BleLog {

    public static boolean isPrint = true;
    private static String defaultTag = "FastBle";

    public static void d(String msg) {
        if (isPrint && msg != null)
            Logger.LogDebug(defaultTag, msg);
    }

    public static void i(String msg) {
        if (isPrint && msg != null)
            Logger.LogInfo(defaultTag, msg);
    }

    public static void w(String msg) {
        if (isPrint && msg != null)
            Log.w(defaultTag, msg);
    }

    public static void e(String msg) {
        if (isPrint && msg != null)
            Logger.LogError(defaultTag, msg);
    }

}
