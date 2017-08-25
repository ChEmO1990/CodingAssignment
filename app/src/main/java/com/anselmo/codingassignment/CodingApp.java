package com.anselmo.codingassignment;

import android.app.Application;
import android.content.ContextWrapper;

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
