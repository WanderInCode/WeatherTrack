package com.example.zhanyh.weathertrack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.zhanyh.weathertrack.controller.GlobalController;
import com.example.zhanyh.weathertrack.fragment.CoordinateFragment;
import com.example.zhanyh.weathertrack.fragment.WaitFragment;
import com.example.zhanyh.weathertrack.interfaces.DataLoadCompleteListener;

/**
 * Created by zhanyh on 15-11-6.
 */
public class TrackActivity extends AppCompatActivity implements DataLoadCompleteListener {

    public static void startTrackActivity(Context context, String provinceName, int cityIndex) {
        Intent intent = new Intent(context, TrackActivity.class);
        intent.putExtra("provinceName", provinceName);
        intent.putExtra("cityIndex", cityIndex);
        context.startActivity(intent);
    }

    private GlobalController mController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        Toolbar toolbar = (Toolbar) findViewById(R.id.track_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mController = WeatherApplication.getControllerInstant(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mController = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        CoordinateFragment coordinateFragment = new CoordinateFragment();
        WaitFragment waitFragment = new WaitFragment();
        transaction.add(R.id.wait_layout, coordinateFragment, "content");
        transaction.add(R.id.wait_layout, waitFragment, "wait");
        transaction.hide(waitFragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        Intent mIntent = getIntent();
        mController.bindWithActivity(this);
        mController.fetchFromLocal(1, mIntent.getStringExtra("provinceName"), mIntent.getIntExtra("cityIndex", -1));
        super.onResume();
    }

    @Override
    protected void onPause() {
        mController.unBindWithActivity();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getActivityIdentification() {
        return 1;
    }

    @Override
    public void onUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CoordinateFragment coordinateFragment =
                        (CoordinateFragment) getSupportFragmentManager()
                                .findFragmentByTag("content");
                coordinateFragment.setTrackData(mController.trackData);
            }
        });
    }
}
