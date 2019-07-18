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

        @BindView(R.id.pass_tv)
        TextView passTv;
        @BindView(R.id.price_tv)
        TextView priceTv;
        @BindView(R.id.num_tv)
        TextView numTv;
        @BindView(R.id.address_tv)
        TextView addressTv;
        @BindView(R.id.type_tv)
        TextView typeTv;
        @BindView(R.id.status_tv)
        TextView statusTv;
        @BindView(R.id.date_begin_tv)
        TextView dateBeginTv;
        @BindView(R.id.date_end_tv)
        TextView dateEndTv;
        @BindView(R.id.order_tv)
        TextView orderTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MarketOrderItem marketOrderItem) {
            passTv.setText(marketOrderItem.getPass());
            priceTv.setText(marketOrderItem.getPrice());
            numTv.setText(marketOrderItem.getNum());
            addressTv.setText(marketOrderItem.getAddress());
            typeTv.setText(marketOrderItem.getType());
            statusTv.setText(marketOrderItem.getStatus());
            dateBeginTv.setText(marketOrderItem.getDateBegin());
            dateEndTv.setText(marketOrderItem.getDateEnd());
            orderTv.setText(marketOrderItem.getOrder());
        }
    }
}
