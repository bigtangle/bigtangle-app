package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.BaseLazyFragment;

public class MarketOverCounterTradingFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    public MarketOverCounterTradingFragment() {
    }

    public static MarketOverCounterTradingFragment newInstance() {
        MarketOverCounterTradingFragment fragment = new MarketOverCounterTradingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_transaction_not_open, container, false);
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
    }
}
