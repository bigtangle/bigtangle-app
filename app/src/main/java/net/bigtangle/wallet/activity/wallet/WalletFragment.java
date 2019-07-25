package net.bigtangle.wallet.activity.wallet;

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
import net.bigtangle.wallet.activity.wallet.fragments.WalletAccountFragment;
import net.bigtangle.wallet.activity.wallet.fragments.WalletSecretkeyFragment;

public class WalletFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionsPagerAdapter mAdapter;
    private boolean isInit = false;

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
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), this);
        mViewPager.setAdapter(mAdapter);
        this.isInit = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isInit) {
                this.mAdapter.notifyDataSetChanged();
            }
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
                return WalletAccountFragment.newInstance();
            }
            if (position == 1) {
                return WalletSecretkeyFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return fragment.getString(R.string.wallet_tab_account);
                case 1:
                    return fragment.getString(R.string.wallet_tab_secretkey);
            }
            return null;
        }
    }
}
