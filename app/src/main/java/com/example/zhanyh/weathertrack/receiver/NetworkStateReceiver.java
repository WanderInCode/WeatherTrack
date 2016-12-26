package com.example.zhanyh.weathertrack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.zhanyh.weathertrack.WeatherApplication;
import com.example.zhanyh.weathertrack.controller.GlobalController;
import com.example.zhanyh.weathertrack.util.Help;

/**
 * Created by zhanyh on 15-10-26.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Help.sendDelayedBroadcast(context);
        WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        boolean setByAlarm = intent.getBooleanExtra("set_by_alarm", false);
        SharedPreferences preferences = context.getSharedPreferences("controller", Context.MODE_PRIVATE);
        long lastTrigger = preferences.getLong("last_trigger", 0);
        if (wifiInfo != null && wifiInfo.getIpAddress() != 0) {
            if (System.currentTimeMillis() - lastTrigger > 1000 * 3600 * 2) {
                preferences.edit().putLong("last_trigger", System.currentTimeMillis()).apply();
                GlobalController mController =
                        WeatherApplication.getControllerInstant(context.getApplicationContext());
                mController.refreshWeather(0, true, true);
            }
        } else if (setByAlarm) {
            if (System.currentTimeMillis() - lastTrigger > 1000 * 3600 * 2 && Help.wifiState(context)) {
                preferences.edit().putLong("last_trigger", System.currentTimeMillis()).apply();
                GlobalController mController =
                        WeatherApplication.getControllerInstant(context.getApplicationContext());
                mController.refreshWeather(0, true, true);
            }
        }
    }
}

