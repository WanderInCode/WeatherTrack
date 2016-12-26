package com.example.zhanyh.weathertrack.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhanyh.weathertrack.R;
import com.example.zhanyh.weathertrack.model.WeatherInfo;
import com.example.zhanyh.weathertrack.view.CartesianCoordinates;

import java.util.List;

/**
 * Created by zhanyh on 15-11-7.
 */
public class CoordinateFragment extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private CartesianCoordinates coordinate;
//    private int[] location = new int[]{0, 0};
    private GestureDetector gesture;
    private static final String TAG = "CoordinateFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coordinate_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        coordinate = (CartesianCoordinates) view.findViewById(R.id.coordinate_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        gesture = new GestureDetector(getContext(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        gesture = null;
    }

    @Override
    public void onResume() {
        super.onResume();
//        coordinate.getLocationInWindow(location);
//        Log.d("location", "X: " + location[0] + ", Y: " + location[1]);
//        Log.d("Left", coordinate.getLeft() + "");
//        Log.d("Left", coordinate.getTop() + "");
        coordinate.setOnTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        coordinate.setOnTouchListener(null);
    }

    public void setTrackData(List<WeatherInfo> data) {
        coordinate.setTrackData(data);
        coordinate.invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (((CartesianCoordinates) v).checkInBox(event)) {
            return gesture.onTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        coordinate.invalidate();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
