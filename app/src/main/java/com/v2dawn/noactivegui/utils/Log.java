package com.v2dawn.noactivegui.utils;

import java.io.File;


public class Log {
    private final static String TAG = "NoActiveGui";
    private static final boolean DEBUG;


    static {
        File config = new File(FreezerConfig.ConfigDir, "debug");
        DEBUG = config.exists();
        i("Debug " + (DEBUG ? "on" : "off"));
    }

    public static void d(String msg) {
        if (DEBUG) {
            String log = TAG + " -> " + msg;

            android.util.Log.d("Xposed", log);

        }
    }

    public static void i(String msg) {
        String log = TAG + " -> " + msg;

        android.util.Log.d("Xposed", log);

    }
}
