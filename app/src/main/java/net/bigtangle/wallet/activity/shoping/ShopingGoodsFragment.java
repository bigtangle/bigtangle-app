package net.bigtangle.wallet.activity.shoping;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.OrderRecord;
import net.bigtangle.core.http.server.resp.OrderdataResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.model.MarketOrderItem;
import net.bigtangle.wallet.activity.shoping.adapter.ShopGoodsItemListAdapter;
import net.bigtangle.wallet.activity.shoping.model.ShopGoogsItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ShopingGoodsFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.goods_text_input)
    TextInputEditText goodsTextInput;

    @BindView(R.id.type_radio_group)
    RadioGroup typeRadioGroup;

    @BindView(R.id.shop_search_button)
    Button searchButton;

    @BindView(R.id.shop_recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.shop_swipe_container)
    SwipeRefreshLayout swipeContainer;

    private List<ShopGoogsItem> itemList;

    private ShopGoodsItemListAdapter mAdapter;

    public static ShopingGoodsFragment newInstance() {
        return new ShopingGoodsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<ShopGoogsItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new ShopGoodsItemListAdapter(getContext(), itemList);
    }

    @Override
    public void onLazyLoad() {
        String state = "";
        for (int i = 0; i < typeRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) typeRadioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                state = radioButton.getText().equals(getContext().getString(R.string.shop_goods_RMB)) ? "shop_goods_RMB" : "shop_goods_bigtang_currency";
                break;
            }
        }
        String type = state;
        String goods = goodsTextInput.getText().toString();

        // TODO 获取商城货物
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.activity_shop_category, container, false);
    }

    @Override
    public void initEvent() {
        this.searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onLazyLoad();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeContainer.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setHasFixedSize(true);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        this.onLazyLoad();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }
}
