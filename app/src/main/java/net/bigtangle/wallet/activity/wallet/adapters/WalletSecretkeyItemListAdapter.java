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

public class WalletSecretkeyItemListAdapter extends RecyclerView.Adapter<WalletSecretkeyItemListAdapter.WalletSecretkeyItemViewHolder> {

    private Context mContext;
    private List<WalletSecretkeyItem> itemList;

    public WalletSecretkeyItemListAdapter(Context context, List<WalletSecretkeyItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public WalletSecretkeyItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_secretkey, parent, false);
        WalletSecretkeyItemViewHolder viewHolder = new WalletSecretkeyItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WalletSecretkeyItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class WalletSecretkeyItemViewHolder extends RecyclerView.ViewHolder {

        private TextView addressTextView;
        private TextView pubKeyTextView;

        public WalletSecretkeyItemViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.WalletSecretkeyItem_address_TextView);
            pubKeyTextView = itemView.findViewById(R.id.WalletSecretkeyItem_pubkey_TextView);
        }

        public void bind(WalletSecretkeyItem walletSecretkeyItem) {
            addressTextView.setText(walletSecretkeyItem.getAddress());
            pubKeyTextView.setText(walletSecretkeyItem.getPubKeyHex());
        }
    }
}
