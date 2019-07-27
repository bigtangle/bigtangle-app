package net.bigtangle.wallet.activity.token;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.SectionsPagerAdapter;

import java.util.ArrayList;

public class TokenFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionsPagerAdapter mAdapter;
    private boolean isInit = false;

    public static TokenFragment newInstance() {
        return new TokenFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_token, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, new String[0]);
        mViewPager.setAdapter(mAdapter);
        this.isInit = true;
    }
}
