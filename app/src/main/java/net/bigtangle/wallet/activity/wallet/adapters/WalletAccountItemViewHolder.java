package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;

public class WalletAccountItemViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;


    public WalletAccountItemViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    public void bind(WalletAccountItem walletAccountItem) {
    }
}
