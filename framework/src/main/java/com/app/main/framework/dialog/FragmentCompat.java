package com.app.main.framework.dialog;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.app.main.framework.baseview.BaseFragment;

import java.util.List;

public class FragmentCompat {

    /**
     * 专用于定制fragment流程
     */
    public static class Flow {
        /**
         * 初次启动fragment
         *
         * @param savedInstanceState 判断是否需要初始化加载
         * @param fragmentManager    管理器
         * @param containerId        容器id
         * @param to                 目标Fragment
         */
        public static void add(Bundle savedInstanceState,
                               FragmentManager fragmentManager,
                               int containerId,
                               Fragment to) {
            if (savedInstanceState == null) {
                add(fragmentManager, containerId, to);
            }
        }

        /**
         * 初次启动fragment
         *
         * @param fragmentManager 管理器
         * @param containerId     容器id
         * @param to              目标Fragment
         */
        public static void add(FragmentManager fragmentManager,
                               int containerId,
                               Fragment to) {
            start(fragmentManager, containerId, null, to);
        }

        /**
         * 切换fragment
         *
         * @param fragmentManager 管理器
         * @param containerId     容器id
         * @param from            被隐藏的fragment
         * @param to              目标Fragment
         */
        public static void toggle(FragmentManager fragmentManager, int containerId, Fragment from, Fragment to) {
            start(fragmentManager, containerId, from, to);
        }

        private static void start(FragmentManager fragmentManager, int containerId, Fragment from, Fragment to) {
            String toName = to.getClass().getName();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            if (from == null) {
                ft.add(containerId, to, toName);
            } else {
                ft.add(containerId, to, toName);
                ft.hide(from);
            }

            //添加至回退站
            ft.addToBackStack(toName);
            ft.commit();
        }
    }

    /**
     * 专用于定制类似微信的tab切换fragment
     */
    public static class Layer {

        /**
         * 初始化fragment，list
         *
         * @param fragmentManager
         * @param containerId
         * @param showPosition
         * @param fragments
         */
        public static void init(FragmentManager fragmentManager,
                                int containerId,
                                int showPosition,
                                List<Fragment> fragments) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            int size = fragments.size();
            for (int i = 0; i < size; i++) {
                Fragment fragment = fragments.get(i);

                String toName = fragment.getClass().getName();
                ft.add(containerId, fragment, toName);

                if (i != showPosition) {
                    ft.hide(fragment);
                } else {
                    fragment.onHiddenChanged(false);
                }
            }
            ft.commit();
        }

        /**
         * @param fragmentManager
         * @param myFragment
         */
        public static void restoreInstance(FragmentManager fragmentManager, List<Fragment> myFragment) {
            List<Fragment> fragments = fragmentManager.getFragments();
            myFragment.clear();
            if (fragments != null && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BaseFragment) {
                        myFragment.add(fragments.indexOf(fragment), fragment);
                    }
                }
            }
        }

        /**
         * 切换fragment
         *
         * @param fragmentManager
         * @param hideFragment
         * @param showFragment
         */
        public static void toggle(FragmentManager fragmentManager, Fragment hideFragment, Fragment showFragment) {
            if (fragmentManager == null || showFragment == hideFragment) {
                return;
            }

            FragmentTransaction ft = fragmentManager.beginTransaction().show(showFragment);

            if (hideFragment == null) {
                List<Fragment> fragments = fragmentManager.getFragments();
                if (fragments != null && fragments.size() > 0) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment != showFragment) {
                            ft.hide(fragment);
                        }
                    }
                }
            } else {
                ft.hide(hideFragment);
            }

            ft.commit();
        }

        public static boolean isCurrent(FragmentManager fragmentManager, Fragment currentPage) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    if (fragment != null && !fragment.isHidden() && fragment == currentPage) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

}
