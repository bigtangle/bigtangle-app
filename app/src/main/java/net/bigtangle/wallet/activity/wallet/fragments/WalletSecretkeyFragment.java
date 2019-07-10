package net.bigtangle.wallet.activity.wallet.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.core.ECKey;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.adapters.WalletSecretkeyItemListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;

import java.util.ArrayList;
import java.util.List;

public class WalletSecretkeyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<WalletSecretkeyItem> walletSecretkeyItems;

    private RecyclerView mSecretkeyRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private WalletSecretkeyItemListAdapter mWalletSecretkeyItemListAdapter;

    public WalletSecretkeyFragment() {
    }

    public static WalletSecretkeyFragment newInstance() {
        WalletSecretkeyFragment fragment = new WalletSecretkeyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        mWalletSecretkeyItemListAdapter = new WalletSecretkeyItemListAdapter(getContext(), walletSecretkeyItems);
    }

    private void initData() {
        if (walletSecretkeyItems == null) {
            walletSecretkeyItems = new ArrayList<WalletSecretkeyItem>();
        }
        walletSecretkeyItems.clear();
        List<ECKey> issuedKeys = WalletContextHolder.get().wallet().walletKeys(WalletContextHolder.getAesKey());
        if (issuedKeys != null && !issuedKeys.isEmpty()) {
            for (ECKey ecKey : issuedKeys) {
                WalletSecretkeyItem walletSecretkeyItem = new WalletSecretkeyItem();
                walletSecretkeyItem.setAddress(ecKey.toAddress(WalletContextHolder.networkParameters).toBase58());
                walletSecretkeyItem.setPubKeyHex(ecKey.getPublicKeyAsHex());
                walletSecretkeyItems.add(walletSecretkeyItem);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet_secretkey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSecretkeyRecyclerView = view.findViewById(R.id.Secretkey_RecyclerView);
        mSwipeRefreshLayout = view.findViewById(R.id.Secretkey_swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new WrapContentLinearLayoutManager(getContext());
        mSecretkeyRecyclerView.setHasFixedSize(true);
        mSecretkeyRecyclerView.setLayoutManager(mLayoutManager);
        mSecretkeyRecyclerView.setAdapter(mWalletSecretkeyItemListAdapter);
    }

    @Override
    public void onRefresh() {
        this.initData();
        mSwipeRefreshLayout.setRefreshing(false);
        mWalletSecretkeyItemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
