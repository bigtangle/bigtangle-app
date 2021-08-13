package net.bigtangle.wallet.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.transaction.TransactionPaymentFragment;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.SectionsPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class TransactionFragment extends BaseLazyFragment {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    private SectionsPagerAdapter mAdapter;

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        this.mTabLayout.setupWithViewPager(mViewPager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(TransactionPaymentFragment.newInstance());
//        fragments.add(TransactionSignatureFragment.newInstance());
//        fragments.add(TransactionBankFragment.newInstance());
//        fragments.add(TransactionHistoryFragment.newInstance());

        String[] title = new String[]{
                this.getString(R.string.transaction_tab_single),
//                this.getString(R.string.transaction_tab_signature),
//                this.getString(R.string.transaction_tab_bank),
//                this.getString(R.string.transaction_tab_history)
        };

        this.mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        this.mViewPager.setAdapter(mAdapter);
        this.mViewPager.setOffscreenPageLimit(4);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void initEvent() {
    }
}
