package net.bigtangle.wallet.activity.transaction;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.BaseLazyFragment;

/**
 * 交易历史记录控件
 *
 * @author lijian
 * @date 2019-07-06 00:04:44
 */
public class TransactionHistoryFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    public TransactionHistoryFragment() {
    }

    public static TransactionHistoryFragment newInstance() {
        TransactionHistoryFragment component = new TransactionHistoryFragment();
        return component;
    }

    @Override
    public void onLazyLoad() {
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }

    @Override
    public void initEvent() {
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
