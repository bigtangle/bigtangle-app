package net.bigtangle.wallet.activity.transaction;

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
import net.bigtangle.wallet.activity.transaction.fragments.TransactionHistoryFragment;
import net.bigtangle.wallet.activity.transaction.fragments.TransactionMultiAddressFragment;
import net.bigtangle.wallet.activity.transaction.fragments.TransactionMultiSignatureFragment;
import net.bigtangle.wallet.activity.transaction.fragments.TransactionSingleFragment;

public class TransactionFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionsPagerAdapter mAdapter;

    public TransactionFragment() {
    }

    public static TransactionFragment newInstance() {
        TransactionFragment fragment = new TransactionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = view.findViewById(R.id.BlockExplorer_viewPager);
        mTabLayout = view.findViewById(R.id.BlockExplorer_tabLayout);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager);
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
                return TransactionSingleFragment.newInstance();
            }
            if (position == 1) {
                return TransactionMultiSignatureFragment.newInstance();
            }
            if (position == 2) {
                return TransactionMultiAddressFragment.newInstance();
            }
            if (position == 3) {
                return TransactionHistoryFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return fragment.getString(R.string.transaction_tab_single);
                case 1:
                    return fragment.getString(R.string.transaction_tab_multi_signature);
                case 2:
                    return fragment.getString(R.string.transaction_tab_multi_address);
                case 3:
                    return fragment.getString(R.string.transaction_tab_history);
            }
            return null;
        }
    }
}
