package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;

public class WalletSecretkeyItemViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private TextView addressTextView;
    private TextView pubkeyTextView;

    public WalletSecretkeyItemViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        addressTextView = itemView.findViewById(R.id.WalletSecretkeyItem_address_TextView);
        pubkeyTextView = itemView.findViewById(R.id.WalletSecretkeyItem_pubkey_TextView);
    }

    public void bind(WalletSecretkeyItem walletSecretkeyItem) {
        addressTextView.setText(walletSecretkeyItem.getAddress());
        pubkeyTextView.setText(walletSecretkeyItem.getPubKeyHex());
    }
}
