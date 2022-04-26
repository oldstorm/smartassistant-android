package com.yctc.zhiting.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.app.main.framework.baseview.BaseFragment;
import com.yctc.zhiting.fragment.SceneItemFragment;

import java.util.ArrayList;
import java.util.List;

public class SceneFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<SceneItemFragment> mFragments;
    private FragmentManager fm;

    public SceneFragmentStatePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fm = fm;
        mFragments = new ArrayList<>();
    }

    public void setFragments(List<SceneItemFragment> fragments) {
        this.mFragments = fragments;
    }

    public void updateFragments(List<SceneItemFragment> fragments) {
        this.mFragments.clear();
        this.mFragments.addAll(fragments);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
