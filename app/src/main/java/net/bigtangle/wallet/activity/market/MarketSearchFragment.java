package net.bigtangle.wallet.activity.market;

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
import android.widget.Switch;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.OrderRecord;
import net.bigtangle.core.http.server.resp.OrderdataResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.adapter.MarketOrderItemListAdapter;
import net.bigtangle.wallet.activity.market.model.MarketOrderItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class MarketSearchFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.address_text_input)
    TextInputEditText addressTextInput;

    @BindView(R.id.state_radio_group)
    RadioGroup stateRadioGroup;

    @BindView(R.id.only_me_switch)
    Switch onlyMeSwitch;

    @BindView(R.id.search_button)
    Button searchButton;

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private List<MarketOrderItem> itemList;

    private MarketOrderItemListAdapter mAdapter;

    public static MarketSearchFragment newInstance() {
        return new MarketSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<MarketOrderItem>();
        }
        this.mAdapter = new MarketOrderItemListAdapter(getContext(), itemList);
    }

    @Override
    public void onLazyLoad() {
        String state = "";
        for (int i = 0; i < stateRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) stateRadioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                state = radioButton.getText().equals(getContext().getString(R.string.publish)) ? "publish" : "match";
                break;
            }
        }

        HashMap<String, Object> requestParam = new HashMap<String, Object>();
        requestParam.put("address", addressTextInput.getText().toString());
        requestParam.put("state", state);
        requestParam.put("spent", "publish".equals(state) ? "false" : "true");
        if (onlyMeSwitch.isChecked()) {
            List<ECKey> walletKeys = WalletContextHolder.get().wallet().walletKeys(WalletContextHolder.getAesKey());
            List<String> addressList = new ArrayList<String>();
            for (ECKey ecKey : walletKeys) {
                addressList.add(ecKey.toAddress(WalletContextHolder.networkParameters).toString());
            }
            requestParam.put("addresses", addressList);
        }

        new HttpNetTaskRequest(this.getContext()).httpRequest(ReqCmd.getOrders, requestParam, new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                try {
                    OrderdataResponse orderdataResponse = Json.jsonmapper().readValue(jsonStr, OrderdataResponse.class);
                    itemList.clear();
                    for (OrderRecord orderRecord : orderdataResponse.getAllOrdersSorted()) {
                        MarketOrderItem marketOrderItem = MarketOrderItem.build(orderRecord);
                        itemList.add(marketOrderItem);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    Log.e(LogConstant.TAG, "reqCmd getOrders failure to parse data", e);
                }
            }
        });
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_search, container, false);
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
