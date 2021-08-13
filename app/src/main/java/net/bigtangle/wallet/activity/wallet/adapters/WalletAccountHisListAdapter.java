package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountHisItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountHisListAdapter extends RecyclerView.Adapter<WalletAccountHisListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<WalletAccountHisItem> itemList;

    public WalletAccountHisListAdapter(Context context, List<WalletAccountHisItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public WalletAccountHisListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_account_his_item, parent, false);
        WalletAccountHisListAdapter.ItemViewHolder viewHolder = new WalletAccountHisListAdapter.ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAccountHisListAdapter.ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.token_id_text_view)
        TextView tokenIdTextView;

        @BindView(R.id.token_name_text_view)
        TextView tokenNameTextView;

        @BindView(R.id.amount_text_view)
        TextView amountTextView;

        @BindView(R.id.address_text_view)
        TextView addressTextView;

        @BindView(R.id.memo_text_view)
        TextView memoTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletAccountHisItem walletAccountHisItem) {
            this.tokenIdTextView.setText(walletAccountHisItem.getTokenId());
            this.tokenNameTextView.setText(walletAccountHisItem.getTokenName());
            this.amountTextView.setText(walletAccountHisItem.getAmount());
            this.addressTextView.setText(walletAccountHisItem.getAddress());
            this.memoTextView.setText(walletAccountHisItem.getMemo());
        }
    }
}
