package com.example.zhanyh.weathertrack;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.zhanyh.weathertrack.controller.GlobalController;

/**
 * Created by zhanyh on 15-10-17.
 */
public class WeatherApplication extends Application {

    private static final String TAG = "WeatherApplication";
    private static GlobalController mController;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static GlobalController getControllerInstant(Context context) {
        if(mController == null) {
            synchronized (GlobalController.class) {
                if(mController == null) {
                    mController = new GlobalController(context.getApplicationContext());
                    return mController;
                }
            }
        }
        return mController;
    }

}
