package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.WalletAccountHisActivity;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountItemListAdapter extends RecyclerView.Adapter<WalletAccountItemListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<WalletAccountItem> itemList;

    public WalletAccountItemListAdapter(Context context, List<WalletAccountItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_account, parent, false);
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

        @BindView(R.id.token_name_text_view)
        TextView tokenNameTextView;

        @BindView(R.id.token_id_text_view)
        TextView tokenidTextView;

        @BindView(R.id.amount_text_view)
        TextView amountTextView;

        @BindView(R.id.item_line)
        LinearLayout itemLine;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletAccountItem item) {
            this.tokenNameTextView.setText(item.getTokenName());
            this.tokenidTextView.setText(item.getTokenId());
            this.amountTextView.setText(item.getValue());

            this.itemLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WalletAccountHisActivity.class);
                    intent.putExtra("tokenId", item.getTokenId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
