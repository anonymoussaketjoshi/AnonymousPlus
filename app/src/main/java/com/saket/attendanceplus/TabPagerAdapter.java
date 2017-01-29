package com.saket.attendanceplus;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by @K@sh on 1/29/2017.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public TabPagerAdapter(FragmentManager fm,int numOfTabs)  {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position)   {
        switch (position)   {
            case 0:
                return new FirstTab();
            case 1:
                return new SecondTab();
            case 2:
                return new ThirdTab();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount()   {
        return this.numOfTabs;
    }

}
