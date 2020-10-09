package net.bigtangle.wallet.activity.shoping.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.Block;
import net.bigtangle.core.Contact;
import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.utils.Json;
import net.bigtangle.core.MultiSignBy;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.core.Transaction;
import net.bigtangle.core.Utils;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.model.MarketOrderItem;
import net.bigtangle.wallet.activity.shoping.model.ShopGoogsItem;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopGoodsItemListAdapter extends RecyclerView.Adapter<ShopGoodsItemListAdapter.ItemViewHolder> {

    private Context mContext;

    private List<ShopGoogsItem> itemList;

    private ShopGoodsItemListAdapter.OnGoodsRemCallbackListener onGoodsRemCallbackListener;

    public ShopGoodsItemListAdapter(Context context, List<ShopGoogsItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_shop_category_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);

        return viewHolder;
    }

    public void setOnGoodsRemCallbackListener(OnGoodsRemCallbackListener onGoodsRemCallbackListener){
        this.onGoodsRemCallbackListener = onGoodsRemCallbackListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_name_text_view)
        TextView goodsNameTextView;

        @BindView(R.id.goods_price_text_view)
        TextView goodsPriceTextView;

        @BindView(R.id.goods_num_text_view)
        TextView goodsNumTextView;

        @BindView(R.id.shop_negative_button)
        Button negativeButton;

        @BindView(R.id.shop_positive_button)
        Button positiveButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(ShopGoogsItem shopGoogsItem) {
            this.goodsNameTextView.setText(shopGoogsItem.getName());
            this.goodsPriceTextView.setText(shopGoogsItem.getPrice());
            this.goodsNumTextView.setText(String.valueOf(shopGoogsItem.getNum()));

            if (this.negativeButton != null) {
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO 立即购买
                        if (onGoodsRemCallbackListener != null) {
                            onGoodsRemCallbackListener.refreshView();
                        }
                    }
                });
            }
            if (this.positiveButton != null){
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO 加入购物车
                        if (onGoodsRemCallbackListener != null) {
                            onGoodsRemCallbackListener.refreshView();
                        }
                    }
                });
            }
        }
    }

    public interface OnGoodsRemCallbackListener {
        void refreshView();
    }
}
