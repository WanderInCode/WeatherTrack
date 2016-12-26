package com.example.zhanyh.weathertrack.interfaces;

/**
 * Created by zhanyh on 15-10-24.
 */
public interface DataDownloadCompleteListener {
    void onNotify(int flag);
    void onErrorOccur();
}
