package net.bigtangle.wallet.activity.shoping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.shoping.adapter.PaymentGoodsItemListAdapter;
import net.bigtangle.wallet.activity.shoping.model.PaymentGoogsItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ShopingGoodsCartFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.shop_payment_button)
    Button paymentButton;

    @BindView(R.id.shop_payment_recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.shop_payment_swipe_container)
    SwipeRefreshLayout swipeContainer;

    private List<PaymentGoogsItem> itemList;

    private PaymentGoodsItemListAdapter mAdapter;

    public static ShopingGoodsCartFragment newInstance() {
        return new ShopingGoodsCartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shoppingCartsInStock();

        if (this.itemList == null) {
            this.itemList = new ArrayList<PaymentGoogsItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new PaymentGoodsItemListAdapter(getContext(), itemList);
    }

    private void startPayment() {
        // TODO 开始支付
    }

    private boolean shoppingCartsInStock() {
        // TODO 获取购物车是否有货物
        return false;
    }

    public void back(Context context){
        Intent intent =  new Intent();
        intent.setClass(context, ShopNoGoodsCartActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    @Override
    public void onLazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.activity_shop_goods_cart, container, false);
    }

    @Override
    public void initEvent() {
        if (this.paymentButton != null){
            this.paymentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPayment();
                }
            });
        }
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
