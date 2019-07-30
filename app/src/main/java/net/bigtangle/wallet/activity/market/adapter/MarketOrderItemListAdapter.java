package net.bigtangle.wallet.activity.market.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.model.MarketOrderItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketOrderItemListAdapter extends RecyclerView.Adapter<MarketOrderItemListAdapter.ItemViewHolder> {

    private Context mContext;

    private List<MarketOrderItem> itemList;

    public MarketOrderItemListAdapter(Context context, List<MarketOrderItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_market_order, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
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

        @BindView(R.id.order_id_text_view)
        TextView orderIdTextView;

        @BindView(R.id.token_id_text_view)
        TextView tokenIdTextView;

        @BindView(R.id.price_text_view)
        TextView priceTextView;

        @BindView(R.id.amount_text_view)
        TextView amountTextView;

        @BindView(R.id.address_text_view)
        TextView addressTextView;

        @BindView(R.id.type_text_view)
        TextView typeTextView;

        @BindView(R.id.status_text_view)
        TextView statusTextView;

        @BindView(R.id.validate_to_text_view)
        TextView validateToTextView;

        @BindView(R.id.validate_from_text_view)
        TextView validateFromTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MarketOrderItem marketOrderItem) {
            this.orderIdTextView.setText(marketOrderItem.getOrderId());
            this.tokenIdTextView.setText(marketOrderItem.getTokenId());
            this.priceTextView.setText(marketOrderItem.getPrice());
            this.amountTextView.setText(String.valueOf(marketOrderItem.getAmount()));
            this.addressTextView.setText(marketOrderItem.getAddress());
            this.typeTextView.setText(marketOrderItem.getType());
            this.statusTextView.setText("");
            this.validateToTextView.setText(marketOrderItem.getValidateTo());
            this.validateFromTextView.setText(marketOrderItem.getValidateFrom());
        }
    }
}
