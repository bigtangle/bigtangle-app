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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.eletac.tronwallet.R;
import com.eletac.tronwallet.WrapContentLinearLayoutManager;
import com.eletac.tronwallet.block_explorer.BlockExplorerUpdater;

public class VoteWitnessesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mWitnesses_RecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText mSearch_EditText;

    private LinearLayoutManager mLayoutManager;
    private WitnessItemListAdapter mWitnessItemListAdapter;

    private WitnessesUpdatedBroadcastReceiver mWitnessesUpdatedBroadcastReceiver;
    private VotesUpdatedBroadcastReceiver mVotesUpdatedBroadcastReceiver;

    private VoteActivity mVoteActivity;

    public VoteWitnessesFragment() {
        // Required empty public constructor
    }

    public static VoteWitnessesFragment newInstance() {
        VoteWitnessesFragment fragment = new VoteWitnessesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVoteActivity = (VoteActivity) getActivity();

        mWitnessesUpdatedBroadcastReceiver = new WitnessesUpdatedBroadcastReceiver();
        mVotesUpdatedBroadcastReceiver = new VotesUpdatedBroadcastReceiver();

        mWitnessItemListAdapter = new WitnessItemListAdapter(getContext(),true, mVoteActivity.getVoteWitnesses());
        mWitnessItemListAdapter.setShowFiltered(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vote_witnesses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWitnesses_RecyclerView = view.findViewById(R.id.VoteWitnesses_votes_recyclerView);
        mSwipeRefreshLayout = view.findViewById(R.id.VoteWitnesses_swipe_container);
        mSearch_EditText = view.findViewById(R.id.VoteWitnesses_search_editText);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new WrapContentLinearLayoutManager(getContext());

        mWitnesses_RecyclerView.setHasFixedSize(true);
        mWitnesses_RecyclerView.setLayoutManager(mLayoutManager);
        mWitnesses_RecyclerView.setAdapter(mWitnessItemListAdapter);

        mSearch_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilteredWitnesses();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateFilteredWitnesses();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mWitnessesUpdatedBroadcastReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mVotesUpdatedBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mWitnessesUpdatedBroadcastReceiver, new IntentFilter(BlockExplorerUpdater.WITNESSES_UPDATED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mVotesUpdatedBroadcastReceiver, new IntentFilter(VoteActivity.VOTES_UPDATED));
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        BlockExplorerUpdater.singleShot(BlockExplorerUpdater.UpdateTask.Witnesses, true);
    }

    private void updateFilteredWitnesses() {
        mWitnessItemListAdapter.notifyDataSetChanged();
    }

    private boolean checkFilterConditions() {
        return true;
    }

    private class WitnessesUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateFilteredWitnesses();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class VotesUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mWitnessItemListAdapter.notifyDataSetChanged();
        }
    }
}
