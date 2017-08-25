package com.anselmo.codingassignment;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.anselmo.codingassignment.utils.Prefs;

/**
 * Created by chemo on 8/24/17.
 */

public class CodingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
