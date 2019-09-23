package net.bigtangle.wallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.shoping.ShopingGoodsCartFragment;
import net.bigtangle.wallet.activity.shoping.ShopingGoodsFragment;
import net.bigtangle.wallet.activity.wallet.WalletAccountFragment;
import net.bigtangle.wallet.activity.wallet.WalletSecretkeyFragment;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.SectionsPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class ShopingFragment extends BaseLazyFragment {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    private SectionsPagerAdapter mAdapter;

    public static ShopingFragment newInstance() {
        return new ShopingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(ShopingGoodsFragment.newInstance());
        fragments.add(ShopingGoodsCartFragment.newInstance());

        String[] title = new String[]{
                this.getString(R.string.shoping_tab_goods),
                this.getString(R.string.shoping_tab_payment)
        };

        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_shoping, container, false);
    }

    @Override
    public void initEvent() {
    }
}
