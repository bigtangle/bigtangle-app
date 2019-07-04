package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eletac.tronwallet.R;

public class AccountItemListAdapter extends RecyclerView.Adapter<AccountItemListAdapter.AccountItemViewHolder> {

    private Context mContext;

    private boolean showFiltered;

    public AccountItemListAdapter(Context context) {
        mContext = context;
        showFiltered = false;
    }

    @NonNull
    @Override
    public AccountItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account_item, parent, false);
        AccountItemViewHolder viewHolder = new AccountItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountItemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AccountItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mAddress_TextView;
        private TextView mBalance_TextView;
        private TextView mAssets_TextView;
        private TextView mVotes_TextView;
        private TextView mLastOperation_TextView;

        public AccountItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mAddress_TextView = itemView.findViewById(R.id.AccountItem_address_textView);
            mBalance_TextView = itemView.findViewById(R.id.AccountItem_balance_textView);
            mAssets_TextView = itemView.findViewById(R.id.AccountItem_assets_textView);
            mVotes_TextView = itemView.findViewById(R.id.AccountItem_votes_textView);
            mLastOperation_TextView = itemView.findViewById(R.id.AccountItem_last_operation_textView);
        }

        public void bind() {
        }
    }

    public boolean isShowFiltered() {
        return showFiltered;
    }

    public void setShowFiltered(boolean showFiltered) {
        this.showFiltered = showFiltered;
    }
}
