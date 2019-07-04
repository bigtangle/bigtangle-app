package com.eletac.tronwallet.block_explorer.contract.contract_type_fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eletac.tronwallet.R;

public class VoteItemListAdapter extends RecyclerView.Adapter<VoteItemListAdapter.VoteItemViewHolder> {

    private Context mContext;

    public VoteItemListAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public VoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_vote_item, parent, false);
        VoteItemViewHolder viewHolder = new VoteItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VoteItemViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class VoteItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mAddress_TextView;
        private TextView mAmount_TextView;

        public VoteItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mAddress_TextView = itemView.findViewById(R.id.Vote_address_textView);
            mAmount_TextView = itemView.findViewById(R.id.Vote_amount_textView);
        }

        public void bind() {
        }
    }
}
