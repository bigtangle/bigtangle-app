package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eletac.tronwallet.R;

import java.util.ArrayList;
import java.util.List;

public class BlockItemListAdapter extends RecyclerView.Adapter<BlockItemListAdapter.BlockItemViewHolder> {

    private Context mContext;
    private Handler mUpdateElapsedTimeHandler;
    private List<BlockItemViewHolder> mViewHolders;
    private Runnable mUpdateElapsedTimeRunnable = new Runnable() {
        public void run() {
            List<BlockItemViewHolder> viewHolders = new ArrayList<>();

            for (BlockItemViewHolder viewHolder : mViewHolders) {
                if (viewHolder != null)
                    viewHolders.add(viewHolder);
            }

            mViewHolders = viewHolders;

            for (BlockItemViewHolder viewHolder : mViewHolders) {
                viewHolder.updateElapsedTime();
            }

            mUpdateElapsedTimeHandler.postDelayed(mUpdateElapsedTimeRunnable, 1010);
        }
    };

    public BlockItemListAdapter(Context context) {
        mContext = context;
        mViewHolders = new ArrayList<>();

        mUpdateElapsedTimeHandler = new Handler();
        mUpdateElapsedTimeHandler.postDelayed(mUpdateElapsedTimeRunnable, 2000);
    }

    @NonNull
    @Override
    public BlockItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_block_item, parent, false);
        BlockItemViewHolder viewHolder = new BlockItemViewHolder(view);
        mViewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BlockItemViewHolder holder, int position) {
    }

    @Override
    public void onViewRecycled(@NonNull BlockItemViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class BlockItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mBlockNumber_TextView;
        private TextView mBlockElapsedTime_TextView;
        private TextView mBlockTransactionsAmount_TextView;
        private TextView mBlockProducerAddress_TextView;

        public BlockItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mBlockNumber_TextView = itemView.findViewById(R.id.Block_num_textView);
            mBlockElapsedTime_TextView = itemView.findViewById(R.id.Block_elapsed_time_textView);
            mBlockTransactionsAmount_TextView = itemView.findViewById(R.id.Block_transactions_amount_textView);
            mBlockProducerAddress_TextView = itemView.findViewById(R.id.Block_producer_address_textView);
        }

        public void bind() {
        }

        public void updateElapsedTime() {
        }
    }
}
