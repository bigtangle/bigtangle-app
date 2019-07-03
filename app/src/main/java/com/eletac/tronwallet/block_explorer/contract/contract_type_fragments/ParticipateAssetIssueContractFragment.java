package com.eletac.tronwallet.block_explorer.contract.contract_type_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eletac.tronwallet.R;
import com.eletac.tronwallet.block_explorer.contract.ContractFragment;

import org.tron.protos.Contract;
import org.tron.protos.Protocol;

import java.text.NumberFormat;
import java.util.Locale;

public class ParticipateAssetIssueContractFragment extends ContractFragment {

    private Contract.ParticipateAssetIssueContract mContract;

    private TextView mTokenTextView;
    private TextView mAmountTextView;
    private TextView mCostTextView;
    private Contract.AssetIssueContract mAssetIssueContract;

    public ParticipateAssetIssueContractFragment() {
        // Required empty public constructor
    }

    public static ParticipateAssetIssueContractFragment newInstance() {
        ParticipateAssetIssueContractFragment fragment = new ParticipateAssetIssueContractFragment();
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
        return inflater.inflate(R.layout.fragment_participate_asset_issue_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTokenTextView = view.findViewById(R.id.ParticipateAssetIssue_asset_textView);
        mAmountTextView = view.findViewById(R.id.ParticipateAssetIssue_amount_textView);
        mCostTextView = view.findViewById(R.id.ParticipateAssetIssue_cost_textView);

        updateUI();
    }

    @Override
    public void setContract(Protocol.Transaction.Contract contract) {
        updateUI();
    }

    public void updateUI() {
        if (mContract != null && getView() != null) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            numberFormat.setMaximumFractionDigits(6);
            mTokenTextView.setText(mContract.getAssetName().toStringUtf8());
            mCostTextView.setText(numberFormat.format(mContract.getAmount() / 1000000D));
        }
    }
}
