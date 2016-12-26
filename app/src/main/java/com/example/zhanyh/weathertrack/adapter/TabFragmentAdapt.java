package com.example.zhanyh.weathertrack.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.zhanyh.weathertrack.fragment.ContentFragment;


/**
 * Created by zhanyh on 15-10-18.
 */
public class TabFragmentAdapt extends FragmentStatePagerAdapter {

    private String[] tabName = {"浙江", "江苏", "山东"};
    private ContentFragment[] fragments = new ContentFragment[tabName.length];

    public TabFragmentAdapt(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return tabName.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ContentFragment.newInstant(tabName[position], position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ContentFragment mFragment = (ContentFragment) super.instantiateItem(container, position);
        fragments[position] = mFragment;
        return mFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragments[position] = null;
        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabName[position];
    }

    public ContentFragment[] getCurrentFragments() {
        return fragments;
    }
}
