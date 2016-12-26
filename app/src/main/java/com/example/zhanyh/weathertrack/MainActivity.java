package com.example.zhanyh.weathertrack;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.zhanyh.weathertrack.adapter.ContentAdapter;
import com.example.zhanyh.weathertrack.adapter.TabFragmentAdapt;
import com.example.zhanyh.weathertrack.controller.GlobalController;
import com.example.zhanyh.weathertrack.fragment.ContentFragment;
import com.example.zhanyh.weathertrack.interfaces.DataLoadCompleteListener;
import com.example.zhanyh.weathertrack.model.WeatherInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoadCompleteListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabFragmentAdapt mTabFragmentAdapt;
    private GlobalController mController;
    private List<List<WeatherInfo>> weatherDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = WeatherApplication.getControllerInstant(getApplicationContext());
        weatherDatas = mController.getWeatherDatas();
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mToggle.syncState();
        mDrawerLayout.setDrawerListener(mToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navi_item_one:
                        Toast.makeText(MainActivity.this, "item one", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navi_item_two:
                        Toast.makeText(MainActivity.this, "item two", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabFragmentAdapt = new TabFragmentAdapt(getSupportFragmentManager());
        mViewPager.setAdapter(mTabFragmentAdapt);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mController = null;
    }

    @Override
    protected void onStart() {
        mController.bindWithActivity(this);
        mController.fetchFromLocal(0);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mController.unBindWithActivity();
        super.onStop();
    }

    public List<WeatherInfo> getCurrentData(int position) {
        return weatherDatas.get(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.update:
                mController.refreshWeather(0, false, false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ContentFragment[] fragments = mTabFragmentAdapt.getCurrentFragments();
                for (ContentFragment fragment : fragments) {
                    if(fragment != null) {
                        ContentAdapter contentAdapter = fragment.getContentAdapter();
                        if(contentAdapter != null) {
                            contentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getActivityIdentification() {
        return 0;
    }
}
