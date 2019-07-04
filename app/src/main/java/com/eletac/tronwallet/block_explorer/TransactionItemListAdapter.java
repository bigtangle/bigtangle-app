package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eletac.tronwallet.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionItemListAdapter extends RecyclerView.Adapter<TransactionItemListAdapter.TransactionItemViewHolder> {

    private Context mContext;
    private ExecutorService mExecutorService;

    public TransactionItemListAdapter(Context context) {
        mContext = context;
        mExecutorService = Executors.newFixedThreadPool(3);
    }

    @NonNull
    @Override
    public TransactionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaction_item, parent, false);
        TransactionItemViewHolder viewHolder = new TransactionItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionItemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TransactionItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private Handler mUpdateConfirmationHandler;
        private UpdateConfirmationRunnable mUpdateConfirmationRunnable;
        private boolean mFirstConfirmationStateLoaded;

        private TextView mTransactionFrom_TextView;
        private TextView mTransactionTo_TextView;
        private TextView mTransactionTimestamp_TextView;
        private TextView mTransactionAmount_TextView;
        private TextView mTransactionAsset_TextView;
        private TextView mTransactionConfirmed_TextView;
        private CardView mTransactionConfirmed_CardView;
        private ProgressBar mTransactionLoadingConfirmation_ProgressBar;

        public TransactionItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mUpdateConfirmationHandler = new Handler();
            mUpdateConfirmationRunnable = new UpdateConfirmationRunnable();

            mTransactionFrom_TextView = itemView.findViewById(R.id.Transaction_from_textView);
            mTransactionTo_TextView = itemView.findViewById(R.id.Transaction_to_textView);
            mTransactionTimestamp_TextView = itemView.findViewById(R.id.Transaction_timestamp_textView);
            mTransactionAmount_TextView = itemView.findViewById(R.id.Transaction_amount_textView);
            mTransactionAsset_TextView = itemView.findViewById(R.id.Transaction_asset_textView);
            mTransactionConfirmed_TextView = itemView.findViewById(R.id.Transaction_confirmed_textView);
            mTransactionConfirmed_CardView = itemView.findViewById(R.id.Transaction_confirmation_CardView);
            mTransactionLoadingConfirmation_ProgressBar = itemView.findViewById(R.id.Transaction_loading_confirmation_progressBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        public void bind() {
        }
    }

    private void loadConfirmation() {
    }

    private class UpdateConfirmationRunnable implements Runnable {

        @Override
        public void run() {
            loadConfirmation();
        }
    }
}
