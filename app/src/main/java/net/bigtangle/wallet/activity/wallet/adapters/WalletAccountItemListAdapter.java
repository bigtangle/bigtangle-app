package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;

import java.util.List;

import butterknife.Bind;
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

        @Bind(R.id.tokenidTextView)
        private TextView tokenidTextView;

        @Bind(R.id.amountTextView)
        private TextView amountTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletAccountItem walletAccountItem) {
            this.tokenidTextView.setText(walletAccountItem.getTokenid());
            this.amountTextView.setText(walletAccountItem.getValue());
        }
    }
}
