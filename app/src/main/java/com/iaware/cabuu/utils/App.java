package com.iaware.cabuu.utils;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by henrique on 10/02/16.
 */
public class App extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Fresco.initialize(this);
        FacebookSdk.sdkInitialize(this);
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
