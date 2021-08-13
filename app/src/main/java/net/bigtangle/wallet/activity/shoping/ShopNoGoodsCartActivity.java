package net.bigtangle.wallet.activity.shoping;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.MainActivity;

import butterknife.BindView;

public class ShopNoGoodsCartActivity extends AppCompatActivity {

    @BindView(R.id.cart_market)
    Button cartMarket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_no_goods_cart);

        this.cartMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopNoGoodsCartActivity.this, MainActivity.class);
                intent.putExtra("id",5);
                startActivity(intent);
            }
        });
	}

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
