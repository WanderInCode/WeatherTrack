package com.example.zhanyh.weathertrack.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhanyh.weathertrack.R;
import com.example.zhanyh.weathertrack.interfaces.ItemClickListener;

/**
 * Created by zhanyh on 15-10-19.
 */
public class ContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView icon;
    TextView place;
    TextView temperature;
    TextView weather;
    TextView wind;
    TextView windScale;
    TextView lastUpdate;
    private ItemClickListener itemClickListener;

    public ContentHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.weather_iv);
        place = (TextView) itemView.findViewById(R.id.place);
        temperature = (TextView) itemView.findViewById(R.id.temperature);
        weather = (TextView) itemView.findViewById(R.id.weather_now);
        wind = (TextView) itemView.findViewById(R.id.wind);
        windScale = (TextView) itemView.findViewById(R.id.wind_scale);
        lastUpdate = (TextView) itemView.findViewById(R.id.last_update);
        itemView.setOnClickListener(this);
    }

    public void setListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
