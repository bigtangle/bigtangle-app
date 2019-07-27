package net.bigtangle.wallet.activity.wallet;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountHisListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountHisItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountHisActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private WalletAccountHisListAdapter mAdapter;

    private List<WalletAccountHisItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletAccountHisItem>();
        }
        setContentView(R.layout.activity_wallet_account_his);
        ButterKnife.bind(this);

        this.swipeContainer.setOnRefreshListener(this);

        this.recyclerViewContainer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        this.recyclerViewContainer.setLayoutManager(layoutManager);

        this.mAdapter = new WalletAccountHisListAdapter(this, this.itemList);
        this.recyclerViewContainer.setAdapter(this.mAdapter);

        this.initData();
    }

    private void initData() {
        String tokenId_ = getIntent().getStringExtra("tokenId");
        List<String> keyStrHex = new ArrayList<String>();
        Wallet wallet = WalletContextHolder.get().wallet();
        for (ECKey ecKey : wallet.walletKeys(WalletContextHolder.getAesKey())) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }
        new HttpNetTaskRequest(this).httpRequest(ReqCmd.getBalances, keyStrHex, new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                try {
                    GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(jsonStr, GetBalancesResponse.class);
                    itemList.clear();
                    for (UTXO utxo : getBalancesResponse.getOutputs()) {
                        Coin coin = utxo.getValue();
                        if (coin.isZero()) {
                            continue;
                        }
                        String balance = coin.toPlainString();
                        byte[] tokenid = coin.getTokenid();
                        String address = utxo.getAddress();

                        WalletAccountHisItem walletAccountHisItem = new WalletAccountHisItem();
                        walletAccountHisItem.setTokenId(Utils.HEX.encode(tokenid));
                        Token token = getBalancesResponse.getTokennames().get(walletAccountHisItem.getTokenId());
                        if (token != null) {
                            walletAccountHisItem.setTokenName(token.getTokenname());
                        } else {
                            walletAccountHisItem.setTokenName(walletAccountHisItem.getTokenId());
                        }
                        walletAccountHisItem.setAddress(address);
                        walletAccountHisItem.setAmount(balance);

                        if (walletAccountHisItem.getTokenId().equals(tokenId_)) {
                            itemList.add(walletAccountHisItem);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    Log.e(LogConstant.TAG, "ReqCmd.getBalances", e);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        this.initData();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }
}
