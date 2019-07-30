package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.SectionsPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class MarketFragment extends BaseLazyFragment {

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
    public void onLazyLoad() {
        mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(MarketSearchFragment.newInstance());
        fragments.add(MarketPublishFragment.newInstance());
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
        mViewPager.setOffscreenPageLimit(5);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market, container, false);
    }

    @Override
    public void initEvent() {
    }
}
