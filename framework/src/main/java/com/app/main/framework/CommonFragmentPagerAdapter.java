package com.app.main.framework;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.main.framework.baseview.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * date : 2021/4/2814:26
 * desc :
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();

//    public CommonFragmentPagerAdapter(FragmentManager fm, BaseFragment... fragments) {
//        super(fm);
//        mFragments.addAll(Arrays.asList(fragments));
//    }
//
//    public CommonFragmentPagerAdapter(FragmentManager fm, Collection<BaseFragment> fragments) {
//        super(fm);
//        mFragments.addAll(fragments);
//    }

    public CommonFragmentPagerAdapter(FragmentManager fm, Collection<BaseFragment> fragments, List<String> titles) {
        super(fm);
        mTitles.clear();
        mTitles.addAll(titles);
        mFragments.addAll(fragments);
    }

    public CommonFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && mTitles.size() > 0) {
            return mTitles.get(position);
        }
        return "";
    }

    public void addFragment(boolean isManual, String title, BaseFragment fragment) {
        if (isManual) {
            mTitles.add(0, title);
            mFragments.add(0, fragment);
        } else {
            mTitles.add(title);
            mFragments.add(fragment);
        }
        notifyDataSetChanged();
    }

    public void removeFragment(int position) {
        mTitles.remove(position);
        mFragments.remove(position);
        notifyDataSetChanged();
    }

    public void removeFragment(String title, BaseFragment fragment){
        mTitles.remove(title);
        mFragments.remove(fragment);
        notifyDataSetChanged();
    }
}


