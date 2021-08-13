package net.bigtangle.wallet.components;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;

/**
 * 多重地址交易控件
 *
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class EmptyFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    public EmptyFragment() {
    }

    public static EmptyFragment newInstance() {
        EmptyFragment fragment = new EmptyFragment();
        return fragment;
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
    public void onRefresh() {
    }
}
