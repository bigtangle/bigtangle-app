package net.bigtangle.wallet.activity.market;

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
        return new MarketFragment();
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
        mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(MarketSearchFragment.newInstance());
        fragments.add(MarketOrderFragment.newInstance());
        fragments.add(MarketOverCounterTradingFragment.newInstance());
        fragments.add(MarketExchangeFragment.newInstance());
        fragments.add(MarketSignatureFragment.newInstance());

        String[] title = new String[]{
                this.getString(R.string.search),
                this.getString(R.string.market_tab_order),
                this.getString(R.string.market_tab_outside_trade),
                this.getString(R.string.market_tab_exchange),
                this.getString(R.string.transaction_tab_signature)
        };

        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        mViewPager.setAdapter(mAdapter);
    }
}
