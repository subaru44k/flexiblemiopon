package com.subaru.flexiblemiopon.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shiny_000 on 2015/02/24.
 */
public class FlexibleFragmentPagerAdaper extends FragmentPagerAdapter {
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public FlexibleFragmentPagerAdaper(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PacketLogFragment();
            case 1:
                return new MainFragment();
            case 2:
                return new ItemFragment();
            default:
                return new MainFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page : " + Integer.toString(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }
}
