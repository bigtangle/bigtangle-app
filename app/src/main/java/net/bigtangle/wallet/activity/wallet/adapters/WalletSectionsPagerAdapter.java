package net.bigtangle.wallet.activity.wallet.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.transaction.components.WalletAccountFragment;
import net.bigtangle.wallet.activity.wallet.components.WalletSecretkeyFragment;

public class WalletSectionsPagerAdapter extends FragmentPagerAdapter {

    private Fragment fragment;

    public WalletSectionsPagerAdapter(FragmentManager fragmentManager, Fragment fragment) {
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
