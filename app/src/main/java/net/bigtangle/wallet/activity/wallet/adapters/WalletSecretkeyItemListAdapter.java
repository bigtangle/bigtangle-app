package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;

import java.util.List;

public class WalletSecretkeyItemListAdapter extends RecyclerView.Adapter<WalletSecretkeyItemViewHolder> {

    private Context mContext;
    private List<WalletSecretkeyItem> mWalletSecretkeyItems;

    public WalletSecretkeyItemListAdapter(Context context, List<WalletSecretkeyItem> mWalletSecretkeyItems) {
        this.mContext = context;
        this.mWalletSecretkeyItems = mWalletSecretkeyItems;
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
        holder.bind(mWalletSecretkeyItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mWalletSecretkeyItems != null ? mWalletSecretkeyItems.size() : 0;
    }
}
