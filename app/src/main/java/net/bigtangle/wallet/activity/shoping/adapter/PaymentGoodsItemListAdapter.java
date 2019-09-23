package net.bigtangle.wallet.activity.shoping.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.shoping.model.PaymentGoogsItem;
import net.bigtangle.wallet.activity.shoping.model.ShopGoogsItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentGoodsItemListAdapter extends RecyclerView.Adapter<PaymentGoodsItemListAdapter.ItemViewHolder> {

    private Context mContext;

    private List<PaymentGoogsItem> itemList;

    private RecyclerView rl;

    private PaymentGoodsItemListAdapter.OnPayRemCallbackListener onPayRemCallbackListener;

    public PaymentGoodsItemListAdapter(Context context, List<PaymentGoogsItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_shop_payment_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);

        return viewHolder;
    }

    public void setOnPayRemCallbackListener(OnPayRemCallbackListener onPayRemCallbackListener){
        this.onPayRemCallbackListener = onPayRemCallbackListener;
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

        @BindView(R.id.payment_goods_id_text_view)
        TextView payGoodIdsTextView;

        @BindView(R.id.payment_name_text_view)
        TextView payNameTextView;

        @BindView(R.id.payment_num_text_view)
        TextView PayNumTextView;

        @BindView(R.id.item_main_point)
        ImageView imageView;

        @BindView(R.id.payment_goods_item_line)
        LinearLayout itemLine;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(PaymentGoogsItem paymentGoogsItem) {
            this.payGoodIdsTextView.setText(paymentGoogsItem.getGoodsId());
            this.payNameTextView.setText(paymentGoogsItem.getName());
            this.PayNumTextView.setText(String.valueOf(paymentGoogsItem.getNum()));
            this.itemLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paymentGoogsItem.setSelect(!paymentGoogsItem.isSelect());
                    imageView.setImageResource(paymentGoogsItem.isSelect()?R.drawable.radio_choose:R.drawable.radio_normal);
                }
            });
            if (onPayRemCallbackListener != null) {
                onPayRemCallbackListener.refreshView();
            }
        }
    }

    public interface OnPayRemCallbackListener {
        void refreshView();
    }
}
