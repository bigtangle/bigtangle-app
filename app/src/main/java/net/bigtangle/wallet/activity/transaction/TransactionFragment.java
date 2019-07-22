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
import net.bigtangle.wallet.activity.transaction.fragments.TransactionBankFragment;
import net.bigtangle.wallet.activity.transaction.fragments.TransactionHistoryFragment;
import net.bigtangle.wallet.activity.transaction.fragments.TransactionPaymentFragment;
import net.bigtangle.wallet.activity.transaction.fragments.TransactionSignatureFragment;

public class TransactionFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionsPagerAdapter mAdapter;

    public static TransactionFragment newInstance() {
        TransactionFragment fragment = new TransactionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                return TransactionPaymentFragment.newInstance();
            }
            if (position == 1) {
                return TransactionSignatureFragment.newInstance();
            }
            if (position == 2) {
                return TransactionBankFragment.newInstance();
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
                    return fragment.getString(R.string.transaction_tab_signature);
                case 2:
                    return fragment.getString(R.string.transaction_tab_bank);
                case 3:
                    return fragment.getString(R.string.transaction_tab_history);
            }
            return null;
        }
    }
}
