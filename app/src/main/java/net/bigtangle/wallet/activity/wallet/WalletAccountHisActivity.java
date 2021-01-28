package net.bigtangle.wallet.activity.wallet;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.utils.Json;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.response.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.MonetaryFormat;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountHisListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountHisItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountHisActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

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

        setSupportActionBar(toolbarLocalMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        this.initData();
    }

    private void initData() {
        String tokenId_ = getIntent().getStringExtra("tokenId");
        List<String> keyStrHex = new ArrayList<String>();
        for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }
        new HttpNetTaskRequest(this).httpRequest(ReqCmd.getBalances, keyStrHex, new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                try {
                    GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(jsonStr, GetBalancesResponse.class);
                    itemList.clear();
                    List<UTXO> utxoList=getBalancesResponse.getOutputs();
                    Collections.sort(utxoList, new Comparator<UTXO>() {
                        @Override
                        public int compare(UTXO order1, UTXO order2) {
                            return order1.getTime() > order2.getTime() ? -1:1;
                        }
                    });
                    for (UTXO utxo : utxoList) {
                        Coin coin = utxo.getValue();
                        if (coin.isZero()) {
                            continue;
                        }
                        Token t = getBalancesResponse.getTokennames().get(
                                Utils.HEX.encode(coin.getTokenid()));
                        String balance = MonetaryFormat.FIAT.noCode().format(
                                coin.getValue(), t.getDecimals());
                        byte[] tokenid = coin.getTokenid();
                        String address = utxo.getAddress();
                        String memo = utxo.memoToString();

                        WalletAccountHisItem walletAccountHisItem = new WalletAccountHisItem();
                        walletAccountHisItem.setTokenId(Utils.HEX.encode(tokenid));
                        Token token = getBalancesResponse.getTokennames().get(walletAccountHisItem.getTokenId());
                        if (token != null) {
                            walletAccountHisItem.setTokenName(token.getTokennameDisplay());
                        } else {
                            walletAccountHisItem.setTokenName(walletAccountHisItem.getTokenId());
                        }
                        walletAccountHisItem.setAddress(address);
                        walletAccountHisItem.setAmount(balance);
                        walletAccountHisItem.setMemo(memo);

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
