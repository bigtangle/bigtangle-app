package net.bigtangle.wallet.activity.shoping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.shoping.adapter.ShopGoodsItemListAdapter;
import net.bigtangle.wallet.activity.shoping.model.PaymentGoogsItem;
import net.bigtangle.wallet.activity.shoping.model.ShopGoogsItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ShopGoodsCartActivity extends AppCompatActivity {

    @BindView(R.id.shop_payment_button)
    Button paymentButton;

    @BindView(R.id.shop_payment_recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.shop_payment_swipe_container)
    SwipeRefreshLayout swipeContainer;

    private List<PaymentGoogsItem> itemList;

    private ShopGoodsItemListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_goods_cart);

		if (!shoppingCartsInStock()){
            Intent intent = new Intent(ShopGoodsCartActivity.this, ShopNoGoodsCartActivity.class);
            startActivity(intent);
            return;
        }

        if (this.itemList == null) {
            this.itemList = new ArrayList<PaymentGoogsItem>();
        }

        this.paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
	}

    private void startPayment() {
        // TODO 开始支付
    }

    private boolean shoppingCartsInStock() {
	    // TODO 获取购物车是否有货物
	    return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}
