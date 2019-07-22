package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.fragment.MarketExchangeFragment;
import net.bigtangle.wallet.activity.market.fragment.MarketOrderFragment;
import net.bigtangle.wallet.activity.market.fragment.MarketOverCounterTradingFragment;
import net.bigtangle.wallet.activity.market.fragment.MarketSearchFragment;
import net.bigtangle.wallet.activity.market.fragment.MarketSignatureFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketFragment extends Fragment {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private SectionsPagerAdapter mAdapter;

    public MarketFragment() {
    }

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), this);
            mViewPager.setAdapter(mAdapter);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment fragment;

        public SectionsPagerAdapter(FragmentManager fragmentManager, Fragment fragment) {
            super(fragmentManager);
            this.fragment = fragment;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MarketSearchFragment.newInstance();
            } else if (position == 1) {
                return MarketOrderFragment.newInstance();
            } else if (position == 2) {
                return MarketOverCounterTradingFragment.newInstance();
            } else if (position == 3) {
                return MarketExchangeFragment.newInstance();
            } else if (position == 4) {
                return MarketSignatureFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "搜索";
                case 1:
                    return "订单";
                case 2:
                    return "场外交易";
                case 3:
                    return "交换";
                case 4:
                    return "签名";
            }
            return null;
        }
    }
}
