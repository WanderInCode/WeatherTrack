package com.example.zhanyh.weathertrack.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.zhanyh.weathertrack.interfaces.DataLoadCompleteListener;
import com.example.zhanyh.weathertrack.model.WeatherInfo;
import com.example.zhanyh.weathertrack.util.Help;

import java.util.List;


/**
 * Created by zhanyh on 15-10-20.
 */
public class GlobalController {

    private Context mContext;
    public List<List<WeatherInfo>> weatherDatas;
    public List<WeatherInfo> trackData;
    private DataLoadCompleteListener weatherListener;
    private ConnectToService connectToService;
    private ConnectToRealm connectToRealm;
    private static final String TAG = "GlobalController";

    public GlobalController(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("controller", Context.MODE_PRIVATE);
        if (preferences.getBoolean("first_run", true)) {
            Help.sendDelayedBroadcast(context);
            preferences.edit().putBoolean("first_run", false).apply();
        }
        mContext = context;
        weatherDatas = Help.initProvinceWeather();
    }

    public List<List<WeatherInfo>> getWeatherDatas() {
        return weatherDatas;
    }

    public void bindWithActivity(DataLoadCompleteListener listener) {
        weatherListener = listener;
    }

    public void unBindWithActivity() {
        weatherListener = null;
    }

    protected void unBind() {
        connectToService.unBind();
    }

    public void refreshWeather(int flag, boolean isAuto, boolean broadcast) {
        if (!Help.networkState(mContext) && !broadcast) {
            Log.d(TAG, "network is disabled");
            return;
        }
        SharedPreferences preferences = mContext.getSharedPreferences("controller", Context.MODE_PRIVATE);
        long lastTime = preferences.getLong("last_time_fetch_datas", 0);
        boolean isSuccess = preferences.getBoolean("update_success", true);
        if (System.currentTimeMillis() - lastTime > 3) {
            updateWeather(flag, preferences);
        } else if (System.currentTimeMillis() - lastTime <= 1000 * 3600 * 3 && isSuccess && !isAuto) {
            Toast.makeText(mContext, "It's unnecessary to update so frequently. Updating every 3h maybe is better.", Toast.LENGTH_SHORT)
                    .show();
        } else if (System.currentTimeMillis() - lastTime <= 1000 * 10 && !isSuccess && !isAuto) {
            Toast.makeText(mContext, "Last update was fail. Just wait another ten second!", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - lastTime > 1000 * 10 && !isSuccess) {
            updateWeather(flag, preferences);
        } else if (System.currentTimeMillis() - lastTime > 1000 * 3600 * 3 && broadcast) {
            updateWeather(flag, preferences);
        }
//        else if (broadcast && System.currentTimeMillis() - lastTime > 1000 * 3600 * 2) {
//            updateWeather(flag, preferences);
//        }
    }

    private void updateWeather(int flag, SharedPreferences preferences) {
        if (connectToService == null) {
            connectToService = new ConnectToService(mContext, this);
        }
        if (connectToRealm == null) {
            connectToRealm = new ConnectToRealm(mContext, this);
        }
        Log.d(TAG, "SERVICE");
        connectToService.bind(flag);
        preferences.edit().putLong("last_time_fetch_datas", System.currentTimeMillis()).apply();
    }

    public void notifyActivityUpdate(int flag) {
        if (weatherListener != null && weatherListener.getActivityIdentification() == flag) {
            weatherListener.onUpdate();
        }
    }

    public void writeDatas() {
        Log.d(TAG, "will unBind excute?");
        connectToRealm.writeToRealm();
    }

    public void fetchFromLocal(int flag) {
        if (connectToRealm == null) {
            connectToRealm = new ConnectToRealm(mContext, this);
        }
        connectToRealm.fetchFromRealm(flag);
    }

    public void fetchFromLocal(int flag, String provinceName, int cityIndex) {
        if (connectToRealm == null) {
            connectToRealm = new ConnectToRealm(mContext, this);
        }
        connectToRealm.getLastWeather(flag, provinceName, cityIndex);
    }
}
