package com.eletac.tronwallet.block_explorer.contract.contract_type_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eletac.tronwallet.R;
import com.eletac.tronwallet.WrapContentLinearLayoutManager;
import com.eletac.tronwallet.block_explorer.contract.ContractFragment;

public class VoteWitnessContractFragment extends ContractFragment {

    private RecyclerView mWitnesses_RecyclerView;
    private VoteItemListAdapter mVoteItemListAdapter;
    private LinearLayoutManager mLayoutManager;

    public VoteWitnessContractFragment() {
        // Required empty public constructor
    }

    public static VoteWitnessContractFragment newInstance() {
        VoteWitnessContractFragment fragment = new VoteWitnessContractFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVoteItemListAdapter = new VoteItemListAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote_witness_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWitnesses_RecyclerView = view.findViewById(R.id.VoteWitnessContract_votes_recyclerView);

        mLayoutManager = new WrapContentLinearLayoutManager(getContext());

        mWitnesses_RecyclerView.setHasFixedSize(true);
        mWitnesses_RecyclerView.setLayoutManager(mLayoutManager);
        mWitnesses_RecyclerView.setAdapter(mVoteItemListAdapter);

        updateUI();
    }

    @Override
    public void setContract() {
        updateUI();
    }

    public void updateUI() {
        mVoteItemListAdapter.notifyDataSetChanged();
    }
}
