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

public class WalletAccountItemListAdapter extends RecyclerView.Adapter<WalletAccountItemListAdapter.WalletAccountItemViewHolder> {

    private Context mContext;
    private List<WalletAccountItem> itemList;

    public WalletAccountItemListAdapter(Context context, List<WalletAccountItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
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
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class WalletAccountItemViewHolder extends RecyclerView.ViewHolder {

        private TextView WalletItem_name_textView;
        private TextView WalletItem_name1_textView;

        public WalletAccountItemViewHolder(View itemView) {
            super(itemView);
            this.WalletItem_name_textView = itemView.findViewById(R.id.WalletItem_name_textView);
            this.WalletItem_name1_textView = itemView.findViewById(R.id.WalletItem_name1_textView);
        }

        public void bind(WalletAccountItem walletAccountItem) {
            this.WalletItem_name_textView.setText(walletAccountItem.getTokenid());
            this.WalletItem_name1_textView.setText(walletAccountItem.getValue());
        }
    }
}
