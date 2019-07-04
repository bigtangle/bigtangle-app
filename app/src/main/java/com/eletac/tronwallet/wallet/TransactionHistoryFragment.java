package com.eletac.tronwallet.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.arasthel.asyncjob.AsyncJob;
import com.eletac.tronwallet.R;
import com.eletac.tronwallet.WrapContentLinearLayoutManager;
import com.eletac.tronwallet.block_explorer.TransactionItemListAdapter;
import com.eletac.tronwallet.wallet.confirm_transaction.ConfirmTransactionActivity;

public class TransactionHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mTransactions_RecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Switch mSentReceivedSwitch;

    private LinearLayoutManager mLayoutManager;
    private TransactionItemListAdapter mTransactionsItemListAdapter;

    private TransactionSentBroadcastReceiver mTransactionSentBroadcastReceiver;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    public static TransactionHistoryFragment newInstance() {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTransactionsItemListAdapter = new TransactionItemListAdapter(getContext());
        mTransactionSentBroadcastReceiver = new TransactionSentBroadcastReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mTransactionSentBroadcastReceiver, new IntentFilter(ConfirmTransactionActivity.TRANSACTION_SENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mTransactionSentBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTransactions_RecyclerView = view.findViewById(R.id.TransactionHistory_transactions_recyclerView);
        mSentReceivedSwitch = view.findViewById(R.id.TransactionHistory_SentReceived_switch);
        //mSwipeRefreshLayout = view.findViewById(R.id.TransactionHistory_swipe_container);

        //mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new WrapContentLinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mTransactions_RecyclerView.setHasFixedSize(true);
        mTransactions_RecyclerView.setLayoutManager(mLayoutManager);
        mTransactions_RecyclerView.setAdapter(mTransactionsItemListAdapter);

        mSentReceivedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loadTransactions();
            }
        });
        loadTransactions();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void loadTransactions() {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
            }
        });
    }


    private class TransactionSentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            loadTransactions();
        }
    }
}
