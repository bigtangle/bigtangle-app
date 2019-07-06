package net.bigtangle.wallet.activity.transaction.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.bigtangle.wallet.R;

import net.bigtangle.wallet.activity.transaction.components.TransactionHistoryFragment;
import net.bigtangle.wallet.activity.transaction.components.TransactionMultiAddressFragment;
import net.bigtangle.wallet.activity.transaction.components.TransactionMultiSignatureFragment;
import net.bigtangle.wallet.activity.transaction.components.TransactionSingleFragment;

public class TransactionSectionsPagerAdapter extends FragmentPagerAdapter {

    private Fragment fragment;

    public TransactionSectionsPagerAdapter(FragmentManager fragmentManager, Fragment fragment) {
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
