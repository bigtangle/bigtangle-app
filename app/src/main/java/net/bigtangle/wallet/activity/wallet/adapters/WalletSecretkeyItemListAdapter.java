package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WalletSecretkeyItemListAdapter extends RecyclerView.Adapter<WalletSecretkeyItemListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<WalletSecretkeyItem> itemList;

    public WalletSecretkeyItemListAdapter(Context context, List<WalletSecretkeyItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_secretkey, parent, false);
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

        @Bind(R.id.addressTextView)
        private TextView addressTextView;

        @Bind(R.id.pubkeyTextView)
        private TextView pubkeyTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletSecretkeyItem walletSecretkeyItem) {
            addressTextView.setText(walletSecretkeyItem.getAddress());
            pubkeyTextView.setText(walletSecretkeyItem.getPubKeyHex());
        }
    }
}
