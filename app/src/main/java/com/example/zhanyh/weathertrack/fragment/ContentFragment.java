package com.example.zhanyh.weathertrack.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhanyh.weathertrack.MainActivity;
import com.example.zhanyh.weathertrack.R;
import com.example.zhanyh.weathertrack.adapter.ContentAdapter;
import com.example.zhanyh.weathertrack.model.WeatherInfo;
import com.example.zhanyh.weathertrack.model.WeatherRawInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanyh on 15-10-18.
 */
public class ContentFragment extends Fragment {

    private ContentAdapter contentAdapter;

    public static ContentFragment newInstant(String name, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("position", position);
        ContentFragment contentFragment = new ContentFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle bundle = getArguments();
        List<WeatherInfo> weatherInfoes = ((MainActivity) getActivity()).getCurrentData(bundle.getInt("position"));
        contentAdapter = new ContentAdapter(getContext(), weatherInfoes);
        mRecyclerView.setAdapter(contentAdapter);
    }

    public ContentAdapter getContentAdapter() {
        return contentAdapter;
    }
}
