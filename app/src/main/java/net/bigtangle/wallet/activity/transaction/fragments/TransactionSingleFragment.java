package net.bigtangle.wallet.activity.transaction.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Address;
import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import java.util.ArrayList;
import java.util.List;

public class TransactionSingleFragment extends Fragment {

    private TextInputEditText toAddressTextInput;
    private Spinner tokenNameSpinner;
    private TextInputEditText amountTextInput;
    private Button sendButton;

    private ArrayAdapter<String> mArrayAdapter;

    private List<String> tokenNames;

    public TransactionSingleFragment() {
    }

    public static TransactionSingleFragment newInstance() {
        TransactionSingleFragment fragment = new TransactionSingleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tokenNames = new ArrayList<>();
        Context context = getContext();
        mArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, tokenNames);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.initData();
    }

    private void initData() {
        if (this.tokenNames == null) {
            this.tokenNames = new ArrayList<String>();
        }
        List<String> keyStrHex = new ArrayList<String>();
        Wallet wallet = WalletContextHolder.get().wallet();
        for (ECKey ecKey : wallet.walletKeys(WalletContextHolder.getAesKey())) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }
        new HttpNetTaskRequest(getContext()).httpRequest(ReqCmd.getBalances, keyStrHex, new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                try {
                    GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(jsonStr, GetBalancesResponse.class);
                    tokenNames.clear();
                    for (UTXO utxo : getBalancesResponse.getOutputs()) {
                        Coin coin = utxo.getValue();
                        if (coin.isZero()) {
                            continue;
                        }
                        byte[] tokenid = coin.getTokenid();
                        tokenNames.add(Utils.HEX.encode(tokenid));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mArrayAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_single, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.toAddressTextInput = view.findViewById(R.id.Transaction_toAddress_TextInput);
        this.tokenNameSpinner = view.findViewById(R.id.Transaction_tokenname_Spinner);
        this.amountTextInput = view.findViewById(R.id.Transaction_amount_TextInput);
        this.sendButton = view.findViewById(R.id.Transaction_send_Button);

        tokenNameSpinner.setAdapter(mArrayAdapter);
        tokenNameSpinner.setSelection(0);
        tokenNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amountTextInput.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                amountTextInput.setEnabled(false);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {
                    @Override
                    public void completeCallback(String jsonStr) {
                        new LovelyInfoDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle("操作成功")
                                .setMessage("钱包进行支付成功")
                                .show();
                        return;
                    }
                }, new HttpRunaExecute() {
                    @Override
                    public void execute() throws Exception {
                        String CONTEXT_ROOT = HttpConnectConstant.HTTP_SERVER_URL;
                        Address destination =
                                Address.fromBase58(WalletContextHolder.networkParameters, toAddressTextInput.getText().toString());

                        Wallet wallet = WalletContextHolder.get().wallet();
                        wallet.setServerURL(CONTEXT_ROOT);

                        Coin amount = Coin.parseCoin(amountTextInput.getText().toString(), Utils.HEX.decode(tokenNameSpinner.getSelectedItem().toString()));
                        long factor = 1;
                        amount = amount.multiply(factor);
                        wallet.pay(WalletContextHolder.getAesKey(), destination, amount, "");
                    }
                }).execute();
            }
        });
    }
}