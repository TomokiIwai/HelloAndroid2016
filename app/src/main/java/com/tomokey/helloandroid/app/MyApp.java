package com.tomokey.helloandroid.app;

import android.app.Application;

/**
 * Custom application class.
 */
public class MyApp extends Application {
    private static MyApp mInstance;

    /**
     * Called when the application is starting
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * Get application instance.
     */
    public static MyApp getInstance() {
        return mInstance;
    }
}
