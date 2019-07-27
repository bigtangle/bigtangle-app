package net.bigtangle.wallet.activity.wallet;

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

public class WalletFragment extends Fragment {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    private SectionsPagerAdapter mAdapter;

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(WalletAccountFragment.newInstance());
        fragments.add(WalletSecretkeyFragment.newInstance());

        String[] title = new String[]{
                this.getString(R.string.wallet_tab_account),
                this.getString(R.string.wallet_tab_secretkey)
        };

        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        mViewPager.setAdapter(mAdapter);
    }
}
