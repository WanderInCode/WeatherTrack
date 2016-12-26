package com.example.zhanyh.weathertrack.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.zhanyh.weathertrack.interfaces.DataDownloadCompleteListener;
import com.example.zhanyh.weathertrack.service.WeatherService;

/**
 * Created by zhanyh on 15-10-22.
 */
public class ConnectToService implements ServiceConnection, DataDownloadCompleteListener {

    private Context mContext;
    private WeatherService.CustomBinder mBinder;
    private GlobalController mController;
    private static final String TAG = "ConnectToService";

    public ConnectToService(Context context, GlobalController controller) {
        mContext = context;
        mController = controller;
    }

    public void bind(int flag) {
        Intent mIntent = new Intent(mContext, WeatherService.class);
        mIntent.putExtra("identification", flag);
        mContext.bindService(mIntent, this, Context.BIND_AUTO_CREATE);
    }

    public void unBind() {
        Log.d(TAG, "unBind will excute");
        mContext.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBinder = (WeatherService.CustomBinder) service;
        mBinder.setDownloadListener(this);
        mBinder.setWeatherData(mController.getWeatherDatas());
        Log.d(TAG, "start connect");
        mBinder.fetchWeather();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBinder = null;
    }

    @Override
    public void onNotify(int flag) {
        mController.notifyActivityUpdate(flag);
        mController.writeDatas();
    }

    @Override
    public void onErrorOccur() {
        unBind();
    }
}
