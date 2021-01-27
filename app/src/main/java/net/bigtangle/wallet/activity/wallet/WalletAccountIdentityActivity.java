package net.bigtangle.wallet.activity.wallet;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import net.bigtangle.apps.data.IdentityData;
import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.response.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.MonetaryFormat;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountHisListAdapter;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountIdentityListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountHisItem;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountIdentiyItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountIdentityActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

    private WalletAccountIdentityListAdapter mAdapter;

    private List<WalletAccountIdentiyItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletAccountIdentiyItem>();
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

        List<IdentityData> identityDatas = new ArrayList<IdentityData>();
        Map<String, Token> tokennames = new HashMap<String, Token>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
                        CommonUtil.identityList(ecKey, ecKey, identityDatas, tokennames);
                    }
                } catch (Exception e) {

                }
            }
        }).start();

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
