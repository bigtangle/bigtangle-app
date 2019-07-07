package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;

import java.util.List;

public class WalletAccountItemListAdapter extends RecyclerView.Adapter<WalletAccountItemViewHolder> {

    private Context mContext;
    private List<WalletAccountItem> mWalletAccountItems;

    public WalletAccountItemListAdapter(Context context, List<WalletAccountItem> mWalletAccountItems) {
        this.mContext = context;
        this.mWalletAccountItems = mWalletAccountItems;
    }

    @NonNull
    @Override
    public WalletAccountItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_account, parent, false);
        WalletAccountItemViewHolder viewHolder = new WalletAccountItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAccountItemViewHolder holder, int position) {
        holder.bind(mWalletAccountItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mWalletAccountItems != null ? mWalletAccountItems.size() : 0;
    }
}
