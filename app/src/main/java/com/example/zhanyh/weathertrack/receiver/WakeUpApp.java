package com.example.zhanyh.weathertrack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.zhanyh.weathertrack.WeatherApplication;
import com.example.zhanyh.weathertrack.controller.GlobalController;
import com.example.zhanyh.weathertrack.util.Help;

/**
 * Created by zhanyh on 15-10-28.
 */
public class WakeUpApp extends BroadcastReceiver {
    private static final String TAG = "WakeUpApp";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "receive");
        if (Help.wifiState(context)) {
            GlobalController mController = WeatherApplication.getControllerInstant(context);
            mController.refreshWeather(3, true, true);
        }
    }
}
