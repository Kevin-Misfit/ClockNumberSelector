package com.pisces.demo;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize font configuration
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontGothamRegular))
                .build()
        );
    }
}
