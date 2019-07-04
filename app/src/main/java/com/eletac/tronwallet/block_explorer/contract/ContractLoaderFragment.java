package com.eletac.tronwallet.block_explorer.contract;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.eletac.tronwallet.R;

public class ContractLoaderFragment extends ContractFragment {

    private TextView mContractNameTextView;
    private FrameLayout mContract_FrameLayout;

    public ContractLoaderFragment() {
    }

    public static ContractLoaderFragment newInstance() {
        ContractLoaderFragment fragment = new ContractLoaderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contract_loader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContractNameTextView = view.findViewById(R.id.Contract_name_textView);
        mContract_FrameLayout = view.findViewById(R.id.Contract_frameLayout);
    }

    @Override
    public void setContract() {
    }
}
