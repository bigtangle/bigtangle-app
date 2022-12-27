package net.bigtangle.wallet.activity.market;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.adapter.MarketPriceListAdapter;
import net.bigtangle.wallet.activity.market.model.MarketPrice;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.FormatUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                BigDecimal priceBigDecimal = new BigDecimal(price);
                String priceTemp = FormatUtil.getDecimalFormat(FormatUtil.getCurrentLocale(getContext())).format(priceBigDecimal.stripTrailingZeros());
                String priceChange = tokenprice.getString("priceChange");
                String executedQuantity = tokenprice.getString("executedQuantity");
                String url = tokenprice.getString("url");
                itemList.add(new MarketPrice(tokenid, tokenname, priceTemp + " " + priceChange + " ", executedQuantity, url));
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
