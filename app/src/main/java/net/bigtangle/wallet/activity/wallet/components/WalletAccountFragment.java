package net.bigtangle.wallet.activity.wallet.components;

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

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.Utils;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountItemListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WalletAccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<WalletAccountItem> itemList;

    private WalletAccountItemListAdapter mWalletAccountItemListAdapter;

    private RecyclerView mAccountsRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;

    public WalletAccountFragment() {
    }

    public static WalletAccountFragment newInstance() {
        WalletAccountFragment fragment = new WalletAccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletAccountItem>();
        }
    }

    private void initData() {
        List<String> keyStrHex = new ArrayList<String>();
        Wallet wallet = WalletContextHolder.get().wallet();
        for (ECKey ecKey : wallet.walletKeys(WalletContextHolder.getAesKey())) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }
        new HttpNetTaskRequest(this.getContext()).httpRequest(ReqCmd.getBalances, keyStrHex, new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                try {
                    GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(jsonStr, GetBalancesResponse.class);
                    itemList.clear();
                    for (Coin coin : getBalancesResponse.getBalance()) {
                        if (!coin.isZero()) {
                            WalletAccountItem walletAccountItem = new WalletAccountItem();
                            walletAccountItem.setTokenid(coin.getTokenHex());
                            walletAccountItem.setTokenname(coin.getTokenHex());
                            walletAccountItem.setValue(coin.toPlainString());
                            itemList.add(walletAccountItem);
                        }
                    }
                    WalletAccountItem walletAccountItem = new WalletAccountItem();
                    walletAccountItem.setValue(String.valueOf(100));
                    walletAccountItem.setTokenid("TWUQcCaf7D9nz3pN9Jw4wT4PUFx7NoKdEy");
                    itemList.add(walletAccountItem);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWalletAccountItemListAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWalletAccountItemListAdapter = new WalletAccountItemListAdapter(getContext(), itemList);

        mSwipeRefreshLayout = view.findViewById(R.id.Accounts_swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAccountsRecyclerView = view.findViewById(R.id.Accounts_recyclerView);
        mLayoutManager = new WrapContentLinearLayoutManager(getContext());
        mAccountsRecyclerView.setHasFixedSize(true);
        mAccountsRecyclerView.setLayoutManager(mLayoutManager);
        mAccountsRecyclerView.setAdapter(mWalletAccountItemListAdapter);
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
        mSwipeRefreshLayout.setRefreshing(false);
        this.initData();
    }
}
