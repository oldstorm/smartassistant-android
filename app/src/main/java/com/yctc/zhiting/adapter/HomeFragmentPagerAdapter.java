package com.yctc.zhiting.adapter;

import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.app.main.framework.baseview.BaseFragment;
import com.yctc.zhiting.entity.mine.LocationBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author : JIM
 * date : 2021/4/2814:26
 * desc :
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments = new ArrayList<>();
    private List<LocationBean> mTitles = new ArrayList<>();
    private FragmentManager fm;

//    public CommonFragmentPagerAdapter(FragmentManager fm, BaseFragment... fragments) {
//        super(fm);
//        mFragments.addAll(Arrays.asList(fragments));
//    }
//
//    public CommonFragmentPagerAdapter(FragmentManager fm, Collection<BaseFragment> fragments) {
//        super(fm);
//        mFragments.addAll(fragments);
//    }

    public HomeFragmentPagerAdapter(FragmentManager fm, Collection<BaseFragment> fragments, List<LocationBean> titles) {
        super(fm);
        this.fm = fm;
        mTitles.clear();
        mTitles.addAll(titles);
        mFragments.addAll(fragments);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String fragmentTag = fragment.getTag();
        if (fragment!=getItem(position)){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            fragment = getItem(position);
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commitAllowingStateLoss();
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && mTitles.size() > 0) {
            return mTitles.get(position).getName();
        }
        return "";
    }
}


