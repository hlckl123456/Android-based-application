package com.ks.placesearch;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;
    public DetailPagerAdapter(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentInfo tab1 = new FragmentInfo();
                return tab1;
            case 1:
                FragmentPhotos tab2 = new FragmentPhotos();
                return tab2;
            case 2:
                FragmentMap tab3 = new FragmentMap();
                return tab3;
            case 3:
                FragmentReviews tab4 = new FragmentReviews();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }

}
