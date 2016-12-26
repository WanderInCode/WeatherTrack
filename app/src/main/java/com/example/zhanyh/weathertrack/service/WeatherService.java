package com.example.zhanyh.weathertrack.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.zhanyh.weathertrack.R;
import com.example.zhanyh.weathertrack.interfaces.DataDownloadCompleteListener;
import com.example.zhanyh.weathertrack.model.WeatherInfo;
import com.example.zhanyh.weathertrack.model.WeatherRawInfo;
import com.example.zhanyh.weathertrack.util.Help;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhanyh on 15-10-21.
 */
public class WeatherService extends Service {

    private final static String TAG = "WeatherService";
    private OkHttpClient mClient;
    private List<List<Request>> requests;
    private List<List<WeatherInfo>> weatherDatas;
    private int total;
    private DataDownloadCompleteListener downloadCompleteListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CustomBinder mBinder = new CustomBinder();
        mBinder.setFlag(intent.getIntExtra("identification", -1));
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mClient = new OkHttpClient();
        mClient.getDispatcher().setMaxRequests(4);
        requests = Help.initRequest();
        total = 0;
        for(List<Request> request : requests) {
            total += request.size();
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic0)
                .setContentTitle("更新天气数据")
                .setWhen(System.currentTimeMillis());
        Notification notification = mBuilder.build();
        startForeground(123, notification);
    }

    private void fetch(final int flag) {
        final int totalRequest = total;
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicBoolean errorOccur = new AtomicBoolean(false);
        String format = "yyyy-MM-dd'T'HH:mm:ssZ";
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setDateFormat(format);
        final Gson gson = gsonBuilder.create();
        final long currentTime = System.currentTimeMillis();
        final List<List<WeatherInfo>> copiedDatas = Help.initProvinceWeather();
        Log.d(TAG, "Before Loop");
        for(int i = 0; i < requests.size(); i++) {
            final int label = i;
            List<Request> province = requests.get(i);
            for(int j = 0; j < province.size(); j++) {
                final int index = j;
                mClient.newCall(province.get(j)).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d("False", "label:" + label + " index:" + index);
                        e.printStackTrace();
                        mClient.cancel(Help.tag);
                        if (errorOccur.compareAndSet(false, true)) {
                            downloadCompleteListener.onErrorOccur();
                            getSharedPreferences("controller", MODE_PRIVATE).edit()
                                    .putBoolean("update_success", false).apply();
                        }
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        WeatherRawInfo weatherRawInfo = gson.fromJson(response.body().charStream(), WeatherRawInfo.class);
                        response.body().close();
                        Log.d(TAG, "label:" + label + " index:" + index);
                        Log.d("Status", weatherRawInfo.status);
                        if(weatherRawInfo.status == null || !weatherRawInfo.status.equals("OK")) {
                            getSharedPreferences("controller", MODE_PRIVATE).edit()
                                    .putBoolean("update_success", false).apply();
                            return;
                        }
//                        Log.d(TAG, "copy");
                        count.incrementAndGet();
                        copiedDatas.get(label).get(index).setStatus(weatherRawInfo.status);
                        copiedDatas.get(label).get(index).setCity_name(weatherRawInfo.weather.get(0).city_name);
                        copiedDatas.get(label).get(index).setLast_update(weatherRawInfo.weather.get(0).last_update);
                        copiedDatas.get(label).get(index).setText(weatherRawInfo.weather.get(0).now.text);
                        copiedDatas.get(label).get(index).setCode(weatherRawInfo.weather.get(0).now.code);
                        copiedDatas.get(label).get(index).setTemperature(weatherRawInfo.weather.get(0).now.temperature);
                        copiedDatas.get(label).get(index).setWind_direction(weatherRawInfo.weather.get(0).now.wind_direction);
                        copiedDatas.get(label).get(index).setWind_scale(weatherRawInfo.weather.get(0).now.wind_scale);
                        copiedDatas.get(label).get(index).setHumidity(weatherRawInfo.weather.get(0).now.humidity);
                        copiedDatas.get(label).get(index).setWind_speed(weatherRawInfo.weather.get(0).now.wind_speed);
                        copiedDatas.get(label).get(index).setVisibility(weatherRawInfo.weather.get(0).now.visibility);
                        copiedDatas.get(label).get(index).setKey(currentTime + copiedDatas.get(label).get(index).getKey());
                        Log.d("Success", label + " " + index + " :" + copiedDatas.get(label).get(index).getKey());
                        copiedDatas.get(label).get(index).setWriteTime(currentTime);
                        weatherRawInfo.status = null;
                        weatherRawInfo.weather.get(0).city_name = null;
                        weatherRawInfo.weather.get(0).last_update = null;
                        weatherRawInfo.weather.get(0).now.text = null;
                        weatherRawInfo.weather.get(0).now.code = null;
                        weatherRawInfo.weather.get(0).now.temperature = null;
                        weatherRawInfo.weather.get(0).now.wind_direction = null;
                        weatherRawInfo.weather.get(0).now.wind_scale = null;
                        weatherRawInfo.weather.get(0).now.humidity = null;
                        weatherRawInfo.weather.get(0).now.wind_speed = null;
                        weatherRawInfo.weather.get(0).now.visibility = null;

                        if (count.compareAndSet(totalRequest, count.get())) {
//                            Log.d(TAG, "atom");
                            for (int i = 0; i < copiedDatas.size(); i++) {
                                for (int j = 0; j < copiedDatas.get(i).size(); j++){
                                    Log.d("COPY",label + " " + index + " :" + copiedDatas.get(i).get(j).getKey());
                                    weatherDatas.get(i).set(j, copiedDatas.get(i).get(j));
                                }
                            }
//                            Log.d(TAG, "Preferences");
                            getSharedPreferences("controller", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("update_success", true)
                                    .putLong("write_time", currentTime)
                                    .apply();
                            downloadCompleteListener.onNotify(flag);
                        }
                    }
                });
            }
        }
    }

    private void setListener(DataDownloadCompleteListener listener) {
        downloadCompleteListener = listener;
    }

    public class CustomBinder extends Binder {

        private int flag;

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public void setDownloadListener(DataDownloadCompleteListener listener) {
            setListener(listener);
        }

        public void fetchWeather() {
            fetch(flag);
        }

        public void setWeatherData(List<List<WeatherInfo>> datas) {
            weatherDatas = datas;
        }
    }
}
