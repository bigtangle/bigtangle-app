package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.utils.MarketOrderItem;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.adapter.MarketOrderItemListAdapter;
import net.bigtangle.wallet.activity.market.adapter.MarketPriceListAdapter;
import net.bigtangle.wallet.activity.market.model.MarketPrice;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class MarketPriceFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;


    private List<MarketPrice> itemList;

    private MarketPriceListAdapter mAdapter;


    public static MarketPriceFragment newInstance() {
        return new MarketPriceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<MarketPrice>();
        }
        setFroceLoadData(true);
        this.mAdapter = new MarketPriceListAdapter(getContext(), itemList);

    }


    @Override
    public void onLazyLoad() {
        itemList.clear();
        initOrderdata();
        this.mAdapter.notifyDataSetChanged();
    }

    private void initOrderdata() {
        try {
            String jsonStr = new URLUtil().calcTokenDisplay().get();
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray tokenList = jsonObject.getJSONArray("tokenList");
            JSONObject data = jsonObject.getJSONObject("data");
            for (int i = 0; i < tokenList.length(); i++) {
                JSONObject token = (JSONObject) tokenList.get(i);
                String tokenid = token.getString("tokenid");
                String tokenname = token.getString("tokennameDisplay");
                JSONObject tokenprice = data.getJSONObject(tokenid);
                String price = tokenprice.getString("price");
                String priceChange = tokenprice.getString("priceChange");
                String executedQuantity = tokenprice.getString("executedQuantity");
                String url = tokenprice.getString("url");
                itemList.add(new MarketPrice(tokenid, tokenname, price + "（" + priceChange + "）", executedQuantity, url));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_price, container, false);
    }

    @Override
    public void initEvent() {


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeContainer.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setHasFixedSize(true);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(mAdapter);
//        helper.attachToRecyclerView(this.recyclerViewContainer);
    }

    @Override
    public void onRefresh() {
        this.initOrderdata();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }
}
