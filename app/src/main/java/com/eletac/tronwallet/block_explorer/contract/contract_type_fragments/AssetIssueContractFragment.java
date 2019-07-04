package com.eletac.tronwallet.block_explorer.contract.contract_type_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.eletac.tronwallet.R;
import com.eletac.tronwallet.block_explorer.contract.ContractFragment;

public class AssetIssueContractFragment extends ContractFragment {

    private EditText mName_EditText;
    private EditText mAbbr_EditText;
    private EditText mSupply_EditText;
    private EditText mURL_EditText;
    private EditText mDesc_EditText;

    private EditText mExchangeTrxAmount_EditText;
    private EditText mExchangeTokenAmount_EditText;
    private TextView mTokenPrice_TextView;

    private EditText mFrozenAmount_EditText;
    private EditText mFrozenDays_EditText;

    private EditText mTotalBandwidth_EditText;
    private EditText mBandwidthPerAccount_EditText;

    private TextView mStart_TextView;
    private TextView mEnd_TextView;

    public AssetIssueContractFragment() {
        // Required empty public constructor
    }

    public static AssetIssueContractFragment newInstance() {
        AssetIssueContractFragment fragment = new AssetIssueContractFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asset_issue_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mName_EditText = view.findViewById(R.id.IssueTokenContract_name_editText);
        mAbbr_EditText = view.findViewById(R.id.IssueTokenContract_abbr_editText);
        mSupply_EditText = view.findViewById(R.id.IssueTokenContract_supply_editText);
        mURL_EditText = view.findViewById(R.id.IssueTokenContract_url_editText);
        mDesc_EditText = view.findViewById(R.id.IssueTokenContract_desc_editText);
        mExchangeTrxAmount_EditText = view.findViewById(R.id.IssueTokenContract_trx_amount_editText);
        mExchangeTokenAmount_EditText = view.findViewById(R.id.IssueTokenContract_token_amount_editText);
        mTokenPrice_TextView = view.findViewById(R.id.IssueTokenContract_price_textView);
        mFrozenAmount_EditText = view.findViewById(R.id.IssueTokenContract_frozen_amount_editText);
        mFrozenDays_EditText = view.findViewById(R.id.IssueTokenContract_frozen_days_editText);
        mTotalBandwidth_EditText = view.findViewById(R.id.IssueTokenContract_total_bandwidth_editText);
        mBandwidthPerAccount_EditText = view.findViewById(R.id.IssueTokenContract_bandwidth_per_account_editText);
        mStart_TextView = view.findViewById(R.id.IssueTokenContract_start_time_textView);
        mEnd_TextView = view.findViewById(R.id.IssueTokenContract_end_time_textView);

        updateUI();
    }

    public void updateUI() {}

    @Override
    public void setContract() {
        updateUI();
    }
}
