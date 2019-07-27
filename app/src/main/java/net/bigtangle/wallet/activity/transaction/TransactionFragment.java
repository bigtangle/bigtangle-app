package net.bigtangle.wallet.activity.transaction;

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

public class TransactionFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(TransactionPaymentFragment.newInstance());
        fragments.add(TransactionSignatureFragment.newInstance());
        fragments.add(TransactionBankFragment.newInstance());
        fragments.add(TransactionHistoryFragment.newInstance());

        String[] title = new String[]{
                this.getString(R.string.transaction_tab_single),
                this.getString(R.string.transaction_tab_signature),
                this.getString(R.string.transaction_tab_bank),
                this.getString(R.string.transaction_tab_history)
        };

        this.mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        this.mViewPager.setAdapter(mAdapter);
    }
}
