package com.example.zhanyh.weathertrack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhanyh.weathertrack.R;
import com.example.zhanyh.weathertrack.TrackActivity;
import com.example.zhanyh.weathertrack.interfaces.ItemClickListener;
import com.example.zhanyh.weathertrack.model.WeatherInfo;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by zhanyh on 15-10-19.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentHolder> implements ItemClickListener {

    private List<WeatherInfo> weatherInfoes;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ContentAdapter(Context context, List<WeatherInfo> data) {
        weatherInfoes = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return weatherInfoes.size();
    }

    @Override
    public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ContentHolder(view);
    }

    @Override
    public void onBindViewHolder(ContentHolder holder, int position) {
        WeatherInfo weatherInfo = weatherInfoes.get(position);
        holder.place.setText(weatherInfo.getCity_name());
        holder.temperature.setText(weatherInfo.getTemperature());
        holder.weather.setText(weatherInfo.getText());
        holder.wind.setText(weatherInfo.getWind_direction());
        holder.windScale.setText(weatherInfo.getWind_scale());
        if (weatherInfo.getLast_update() == null) {
            holder.lastUpdate.setText("");
        } else {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            holder.lastUpdate.setText(dateFormat.format(weatherInfo.getLast_update()));
            holder.setListener(this);
        }
        String iconName = weatherInfo.getCode();
        int iconId;
        iconId = mContext.getResources().getIdentifier("com.example.zhanyh.weathertrack:drawable/" + "ic" + iconName, null, null);
        if(iconId == 0) {
            iconId = R.drawable.ic99;
        }
        holder.icon.setImageResource(iconId);
    }

    @Override
    public void onItemClick(int position) {
        TrackActivity.startTrackActivity(mContext, weatherInfoes.get(position).getProvince_name(), weatherInfoes.get(position).getCity_index());
    }
}
