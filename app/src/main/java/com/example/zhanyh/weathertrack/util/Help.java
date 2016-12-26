package com.example.zhanyh.weathertrack.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.zhanyh.weathertrack.model.WeatherInfo;
import com.example.zhanyh.weathertrack.receiver.WakeUpApp;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Object;
import java.util.Map;

/**
 * Created by zhanyh on 15-10-20.
 */
public class Help {

    private static final String TAG = "HELP";

    public static Object tag = new Object();

    public static Map<String, String[]> provinceTable;

    private static String[] provinceOneList
            = {"杭州", "湖州", "嘉兴", "宁波", "绍兴", "台州", "温州", "丽水", "金华", "衢州", "舟山"};

    private static String[] provinceTwoList
            = {"南京", "无锡", "镇江", "苏州", "南通", "扬州", "盐城", "徐州", "淮安", "连云港", "常州", "泰州", "宿迁"};

    private static String[] provinceThreeList
            = {"济南", "青岛", "淄博", "德州", "潍坊", "济宁", "泰安", "临沂", "菏泽", "滨州", "东营", "威海", "枣庄", "日照", "莱芜", "聊城"};

    static {
        provinceTable = new HashMap<>();
        provinceTable.put("浙江", provinceOneList);
        provinceTable.put("江苏", provinceTwoList);
        provinceTable.put("山东", provinceThreeList);
    }

    private static List<WeatherInfo> initProvinceList(String provinceName, String[] cityNames, String provinceType) {
        List<WeatherInfo> province = new ArrayList<>();
        for (int i = 0; i < cityNames.length; i++) {
            WeatherInfo city = new WeatherInfo();
            city.setProvince_name(provinceName);
            city.setCity_name(cityNames[i]);
            city.setCity_index(i);
            if (i / 10 == 0) {
                city.setKey(provinceType + "0" + i);
            } else {
                city.setKey(provinceType + i);
            }
            province.add(city);
        }
        return province;
    }

    public static List<List<WeatherInfo>> initProvinceWeather() {
        List<List<WeatherInfo>> weatherInfoes = new ArrayList<>();
        weatherInfoes.add(initProvinceList("浙江", provinceOneList, "00"));
        weatherInfoes.add(initProvinceList("江苏", provinceTwoList, "01"));
        weatherInfoes.add(initProvinceList("山东", provinceThreeList, "02"));
        return weatherInfoes;
    }

    public static List<List<Request>> initRequest() {
        String urlHead = "https://api.thinkpage.cn/v2/weather/now.json?city=";
        String urlTail = "&language=zh-chs&unit=c&key=F3O4D5CDVE";
        List<Request> requestOne = new ArrayList<>();
        List<Request> requestTwo = new ArrayList<>();
        List<Request> requestThree = new ArrayList<>();
        List<List<Request>> requests = new ArrayList<>();
        for (String cityName : provinceOneList) {
            Request request = new Request.Builder()
                    .url(urlHead + cityName + urlTail)
                    .tag(tag)
                    .build();
            requestOne.add(request);
        }
        requests.add(requestOne);
        for (String cityName : provinceTwoList) {
            Request request = new Request.Builder()
                    .url(urlHead + cityName + urlTail)
                    .tag(tag)
                    .build();
            requestTwo.add(request);
        }
        requests.add(requestTwo);
        for (String cityName : provinceThreeList) {
            Request request = new Request.Builder()
                    .url(urlHead + cityName + urlTail)
                    .tag(tag)
                    .build();
            requestThree.add(request);
        }
        requests.add(requestThree);
        return requests;
    }

    public static boolean networkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    public static boolean wifiState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isAvailable()) {
            return true;
        }
//        Log.d(TAG, networkInfo.isAvailable() + "");
        return false;
    }

    public static void sendDelayedBroadcast(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long setTime = 1000 * 3600 * 3;
//        long triggerTime = System.currentTimeMillis() + setTime;
        Intent intent = new Intent(context.getApplicationContext(), WakeUpApp.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("com.example.zhanyh.weather.WAKEUP");
//        intent.putExtra("set_by_alarm", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1562, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60, setTime, pendingIntent);
    }
}
