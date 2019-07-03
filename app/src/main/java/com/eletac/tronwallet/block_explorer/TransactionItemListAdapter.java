package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.eletac.tronwallet.R;

import org.tron.api.GrpcAPI;
import org.tron.protos.Protocol;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionItemListAdapter extends RecyclerView.Adapter<TransactionItemListAdapter.TransactionItemViewHolder> {

    private Context mContext;
    private List<GrpcAPI.TransactionExtention> mTransactions;
    private ExecutorService mExecutorService;
    private List<Protocol.Transaction> mConfirmedTransactions;

    public TransactionItemListAdapter(Context context, List<GrpcAPI.TransactionExtention> transactions) {
        mContext = context;
        mTransactions = transactions;
        mConfirmedTransactions = new ArrayList<>();
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
        holder.bind(mTransactions.get(position));
    }

    @Override
    public int getItemCount() {
        return mTransactions != null ? mTransactions.size() : 0;
    }

    public class TransactionItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private Protocol.Transaction mTransaction;
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
            mTransaction = null;
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
                    if (mTransaction != null) {
                        Intent intent = new Intent(mContext, TransactionViewerActivity.class);
                        intent.putExtra(TransactionViewerActivity.TRANSACTION_DATA, mTransaction.toByteArray());
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        public void bind(GrpcAPI.TransactionExtention transaction) {
            mTransaction = transaction.getTransaction();
            mFirstConfirmationStateLoaded = false;

            mUpdateConfirmationHandler.removeCallbacks(mUpdateConfirmationRunnable);
            mUpdateConfirmationHandler.post(mUpdateConfirmationRunnable);

            if (mTransaction.getRawData().getContractCount() > 0) {
                Protocol.Transaction.Contract contract = mTransaction.getRawData().getContract(0);

                String from = "", to = "", contract_desc = "";
                String amount_prefix = "";
                double amount = 0;

                // @TODO Setup other contracts

                NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                numberFormat.setMaximumFractionDigits(6);

                mTransactionFrom_TextView.setText(from);
                mTransactionTo_TextView.setText(to);
                mTransactionAmount_TextView.setText((amount != -1 ? numberFormat.format(amount) : "") + " " + amount_prefix);
                mTransactionAsset_TextView.setText(contract_desc);

                long timestamp = mTransaction.getRawData().getTimestamp();
                if (timestamp == 0) {
                    try {
                        for (GrpcAPI.BlockExtention block : BlockExplorerUpdater.getBlocks()) {
                            for (GrpcAPI.TransactionExtention blockTransaction : block.getTransactionsList()) {
                                if (blockTransaction.equals(transaction)) {
                                    timestamp = block.getBlockHeader().getRawData().getTimestamp();
                                    break;
                                }
                            }
                            if (timestamp != 0) {
                                break;
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }
                }
                mTransactionTimestamp_TextView.setText(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(new Date(timestamp)));
            }
        }

        private void loadConfirmation() {
            if (mConfirmedTransactions.contains(mTransaction)) {
                mTransactionConfirmed_TextView.setText(R.string.confirmed);
                mTransactionConfirmed_CardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.positive));
                mTransactionLoadingConfirmation_ProgressBar.setVisibility(View.GONE);
            } else {
                if (!mFirstConfirmationStateLoaded) {
                    mTransactionConfirmed_CardView.setVisibility(View.GONE);
                    mTransactionLoadingConfirmation_ProgressBar.setVisibility(View.VISIBLE);
                }

                AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                    @Override
                    public void doOnBackground() {
                    }
                }, mExecutorService);
            }
        }

        private class UpdateConfirmationRunnable implements Runnable {

            @Override
            public void run() {
                loadConfirmation();
            }
        }
    }
}
