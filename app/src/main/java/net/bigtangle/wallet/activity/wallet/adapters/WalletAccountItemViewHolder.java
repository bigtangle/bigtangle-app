package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;

public class WalletAccountItemViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;

    private TextView WalletItem_name_textView;
    private TextView WalletItem_name1_textView;


    public WalletAccountItemViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();

        this.WalletItem_name_textView = itemView.findViewById(R.id.WalletItem_name_textView);
        this.WalletItem_name1_textView = itemView.findViewById(R.id.WalletItem_name1_textView);
    }

    public void bind(WalletAccountItem walletAccountItem) {
        this.WalletItem_name_textView.setText(walletAccountItem.getTokenid());
        this.WalletItem_name1_textView.setText(walletAccountItem.getValue());
    }
}
