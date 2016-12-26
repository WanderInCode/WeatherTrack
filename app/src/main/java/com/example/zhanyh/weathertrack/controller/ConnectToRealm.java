package com.example.zhanyh.weathertrack.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.zhanyh.weathertrack.model.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by zhanyh on 15-10-25.
 */
public class ConnectToRealm {

    private Context mContext;
    private GlobalController mController;
    private Realm realm;
    private static final String TAG = "ConnectToRealm";

    public ConnectToRealm(Context context, GlobalController controller) {
        mContext = context;
        mController = controller;
    }

    private void openRealm() {
        realm = Realm.getInstance(mContext);
    }

    private void closeRealm() {
        realm.close();
        realm = null;
    }

    public void writeToRealm() {
        Log.d(TAG, "write into realm");
        List<List<WeatherInfo>> weatherDatas = mController.getWeatherDatas();
        openRealm();
        realm.beginTransaction();
        for (List<WeatherInfo> weatherInfos : weatherDatas) {

            try {
                realm.copyToRealm(weatherInfos);
            } catch (Exception e) {
                e.printStackTrace();
                for (WeatherInfo weatherInfo : weatherInfos) {
                    Log.d(TAG, weatherInfo.getKey());
                    Log.d(TAG, weatherInfo.getText());
                }
            }
        }
        realm.commitTransaction();
        closeRealm();
        mController.unBind();
    }

    public void fetchFromRealm(final int flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("controller", Context.MODE_PRIVATE);
        final long lastTime = preferences.getLong("last_time_fetch_datas", 0);
        final long lastWriteTime = preferences.getLong("write_time", 0);
        if (lastWriteTime == 0 || lastTime == 0) {
            mController.refreshWeather(flag, true, false);
            return;
        }
        final List<List<WeatherInfo>> weatherDatas = mController.getWeatherDatas();
        new Thread(new Runnable() {
            @Override
            public void run() {
                openRealm();
                RealmResults<WeatherInfo> realmResults = realm.where(WeatherInfo.class)
                        .equalTo("writeTime", lastWriteTime)
                        .findAll();
                RealmResults<WeatherInfo> provinceOne = realmResults.where()
                        .equalTo("province_name", "浙江")
                        .findAll();
                provinceOne.sort("city_index");
                RealmResults<WeatherInfo> provinceTwo = realmResults.where()
                        .equalTo("province_name", "江苏")
                        .findAll();
                provinceTwo.sort("city_index");
                RealmResults<WeatherInfo> provinceThree = realmResults.where()
                        .equalTo("province_name", "山东")
                        .findAll();
                provinceThree.sort("city_index");
                copyRealmData(provinceOne, weatherDatas.get(0));
                copyRealmData(provinceTwo, weatherDatas.get(1));
                copyRealmData(provinceThree, weatherDatas.get(2));
                mController.notifyActivityUpdate(flag);
                closeRealm();
            }
        }).start();
    }

    public void getLastWeather(final int flag, final String provinceName, final int cityIndex) {
        mController.trackData = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                openRealm();
                RealmResults<WeatherInfo> realmResults = realm.where(WeatherInfo.class)
                        .equalTo("province_name", provinceName)
                        .equalTo("city_index", cityIndex)
                        .findAllSorted("writeTime", true);
                for (int i = 0; i < realmResults.size(); i++) {
                    WeatherInfo dst = new WeatherInfo();
                    WeatherInfo src = realmResults.get(i);
                    dst.setCity_name(src.getCity_name());
                    dst.tempDigit = Integer.valueOf(src.getTemperature());
                    dst.setLast_update(src.getLast_update());
                    mController.trackData.add(dst);
                }
                closeRealm();
                mController.notifyActivityUpdate(flag);
            }
        }).start();
    }

    private void copyRealmData(RealmResults<WeatherInfo> src, List<WeatherInfo> dst) {
        for (int i = 0; i < src.size(); i++) {
            WeatherInfo srcObj = src.get(i);
            WeatherInfo dstObj = dst.get(i);
            dstObj.setKey(srcObj.getKey());
            dstObj.setWriteTime(srcObj.getWriteTime());
            dstObj.setProvince_name(srcObj.getProvince_name());
            dstObj.setCity_name(srcObj.getCity_name());
            dstObj.setCity_index(srcObj.getCity_index());
            dstObj.setLast_update(srcObj.getLast_update());
            dstObj.setText(srcObj.getText());
            dstObj.setCode(srcObj.getCode());
            dstObj.setTemperature(srcObj.getTemperature());
            dstObj.setWind_direction(srcObj.getWind_direction());
            dstObj.setWind_speed(srcObj.getWind_speed());
            dstObj.setWind_scale(srcObj.getWind_scale());
            dstObj.setHumidity(srcObj.getHumidity());
            dstObj.setVisibility(srcObj.getVisibility());
        }
    }

}
