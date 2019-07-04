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

public class FreezeContractFragment extends ContractFragment {

    private TextView mAmountTextView;
    private TextView mDaysTextView;

    public FreezeContractFragment() {
        // Required empty public constructor
    }

    public static FreezeContractFragment newInstance() {
        FreezeContractFragment fragment = new FreezeContractFragment();
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
        return inflater.inflate(R.layout.fragment_freeze_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAmountTextView = view.findViewById(R.id.FreezeContract_amount_textView);
        mDaysTextView = view.findViewById(R.id.FreezeContract_days_textView);

        updateUI();
    }

    @Override
    public void setContract() {
        updateUI();
    }

    public void updateUI() {
    }
}
