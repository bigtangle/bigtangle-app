package net.bigtangle.wallet.activity.wallet.components;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.Utils;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.activity.MainActivity;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountItemListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.OKHttpListener;
import net.bigtangle.wallet.core.http.OKHttpUitls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WalletAccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<WalletAccountItem> walletAccountItems;

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
        initData();
    }

    private void initData() {
        if (this.walletAccountItems == null) {
            this.walletAccountItems = new ArrayList<WalletAccountItem>();
        }

//        WalletAccountItem walletAccountItem = new WalletAccountItem();
//        walletAccountItem.setValue(String.valueOf(100));
//        walletAccountItem.setTokenid("TWUQcCaf7D9nz3pN9Jw4wT4PUFx7NoKdEy");
//        walletAccountItems.add(walletAccountItem);

        List<String> keyStrHex = new ArrayList<String>();
        Wallet wallet = WalletContextHolder.get().wallet();
        for (ECKey ecKey : wallet.walletKeys(WalletContextHolder.getAesKey())) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }
        try {
            OKHttpUitls.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getBalances.name(),
                    Json.jsonmapper().writeValueAsString(keyStrHex).getBytes(), new OKHttpListener() {
                        @Override
                        public void handleMessage(String response) {
                            try {
                                Map<String, String> tokenNameResult = OKHttpUitls.getTokenHexNameMap();
                                GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);

                                walletAccountItems.clear();
                                for (Coin coin : getBalancesResponse.getBalance()) {
                                    if (!coin.isZero()) {
                                        WalletAccountItem walletAccountItem = new WalletAccountItem();
                                        walletAccountItem.setTokenid(coin.getTokenHex());
                                        walletAccountItem.setTokenname(tokenNameResult.get(coin.getTokenHex()));
                                        walletAccountItem.setValue(coin.toPlainString());
                                        walletAccountItems.add(walletAccountItem);
                                    }
                                }

                                WalletAccountItem walletAccountItem = new WalletAccountItem();
                                walletAccountItem.setValue(String.valueOf(100));
                                walletAccountItem.setTokenid("TWUQcCaf7D9nz3pN9Jw4wT4PUFx7NoKdEy");
                                walletAccountItems.add(walletAccountItem);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        WalletAccountItemListAdapter mWalletAccountItemListAdapter = new WalletAccountItemListAdapter(getContext(), walletAccountItems);
                                        mAccountsRecyclerView.setAdapter(mWalletAccountItemListAdapter);
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getActivity(), "请求服务器数据错误", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAccountsRecyclerView = view.findViewById(R.id.Accounts_recyclerView);
        mSwipeRefreshLayout = view.findViewById(R.id.Accounts_swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new WrapContentLinearLayoutManager(getContext());
        mAccountsRecyclerView.setHasFixedSize(true);
        mAccountsRecyclerView.setLayoutManager(mLayoutManager);
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
