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

import org.tron.protos.Contract;
import org.tron.protos.Protocol;

import java.text.NumberFormat;
import java.util.Locale;

public class TransferAssetContractFragment extends ContractFragment {

    private Contract.TransferAssetContract mContract;

    private TextView mAmount_TextView;
    private TextView mSymbol_TextView;
    private TextView mFrom_TextView;
    private TextView mFromName_TextView;
    private TextView mTo_TextView;
    private TextView mToName_TextView;

    public TransferAssetContractFragment() {
        // Required empty public constructor
    }

    public static TransferAssetContractFragment newInstance() {
        TransferAssetContractFragment fragment = new TransferAssetContractFragment();
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
        return inflater.inflate(R.layout.fragment_transfer_asset_contract, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAmount_TextView = view.findViewById(R.id.TransferAssetContract_amount_textView);
        mSymbol_TextView = view.findViewById(R.id.TransferAssetContract_symbol_textView);
        mFrom_TextView = view.findViewById(R.id.TransferAssetContract_from_textView);
        mFromName_TextView = view.findViewById(R.id.TransferAssetContract_from_name_textView);
        mTo_TextView = view.findViewById(R.id.TransferAssetContract_to_textView);
        mToName_TextView = view.findViewById(R.id.TransferAssetContract_to_name_textView);

        updateUI();
    }

    @Override
    public void setContract(Protocol.Transaction.Contract contract) {
        updateUI();
    }

    public void updateUI() {
        if(mContract != null && getView() != null) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            numberFormat.setMaximumFractionDigits(6);
            mAmount_TextView.setText(numberFormat.format(mContract.getAmount()));
            mSymbol_TextView.setText(mContract.getAssetName().toStringUtf8());

            mFromName_TextView.setVisibility(View.GONE);
            mToName_TextView.setVisibility(View.GONE);

        }
    }

    private void loadAccountNames() {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {

                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                    }
                });
            }
        });
    }
}
