package net.bigtangle.wallet.components;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;

    private String[] title;

    public SectionsPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments, String[] title) {
        super(fragmentManager);
        this.fragments = fragments;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.title[position];
    }
}
