package com.eletac.tronwallet.block_explorer.contract.contract_type_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.eletac.tronwallet.R;
import com.eletac.tronwallet.block_explorer.contract.ContractFragment;

public class TransferContractFragment extends ContractFragment {

    private TextView mAmount_TextView;
    private TextView mFrom_TextView;
    private TextView mFromName_TextView;
    private TextView mTo_TextView;
    private TextView mToName_TextView;

    public TransferContractFragment() {
        // Required empty public constructor
    }

    public static TransferContractFragment newInstance() {
        TransferContractFragment fragment = new TransferContractFragment();
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
        return inflater.inflate(R.layout.fragment_transfer_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAmount_TextView = view.findViewById(R.id.TransferContract_amount_textView);
        mFrom_TextView = view.findViewById(R.id.TransferContract_from_textView);
        mFromName_TextView = view.findViewById(R.id.TransferContract_from_name_textView);
        mTo_TextView = view.findViewById(R.id.TransferContract_to_textView);
        mToName_TextView = view.findViewById(R.id.TransferContract_to_name_textView);

        updateUI();
    }

    @Override
    public void setContract() {
        updateUI();
    }

    public void updateUI() {
    }

    private void loadAccountNames() {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
            }
        });
    }
}
