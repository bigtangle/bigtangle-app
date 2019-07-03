package com.eletac.tronwallet.block_explorer.contract;

import android.support.v4.app.Fragment;

import org.tron.protos.Protocol;

public abstract class ContractFragment extends Fragment {
    public abstract void setContract(Protocol.Transaction.Contract contract);
}
