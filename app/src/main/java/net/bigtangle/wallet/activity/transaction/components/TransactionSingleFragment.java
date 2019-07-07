package net.bigtangle.wallet.activity.transaction.components;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import net.bigtangle.core.Address;
import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.exception.InsufficientMoneyException;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.OKHttpListener;
import net.bigtangle.wallet.core.http.OKHttpUitls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionSingleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextInputEditText transaction_toAddress_TextInput;
    private Spinner transaction_tokenname_Spinner;
    private TextInputEditText transaction_amount_TextInput;
    private Button transaction_send_Button;
    ArrayAdapter<String> mArrayAdapter;

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
        try {
            OKHttpUitls.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getBalances.name(),
                    Json.jsonmapper().writeValueAsString(keyStrHex).getBytes(), new OKHttpListener() {
                        @Override
                        public void handleMessage(String response) {
                            Map<String, String> tokenNameResult = OKHttpUitls.getTokenHexNameMap();
                            try {
                                GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);

                                for (UTXO utxo : getBalancesResponse.getOutputs()) {
                                    Coin coin = utxo.getValue();
                                    if (coin.isZero()) {
                                        continue;
                                    }
                                    byte[] tokenid = coin.getTokenid();
                                    //String tokenname = tokenNameResult.get(Utils.HEX.encode(tokenid));
                                    tokenNames.add(Utils.HEX.encode(tokenid));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getActivity(), "请求服务器数据错误", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_single, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.transaction_toAddress_TextInput = view.findViewById(R.id.Transaction_toAddress_TextInput);
        this.transaction_tokenname_Spinner = view.findViewById(R.id.Transaction_tokenname_Spinner);
        this.transaction_amount_TextInput = view.findViewById(R.id.Transaction_amount_TextInput);
        this.transaction_send_Button = view.findViewById(R.id.Transaction_send_Button);

        transaction_tokenname_Spinner.setAdapter(mArrayAdapter);
        transaction_tokenname_Spinner.setSelection(0);
        transaction_tokenname_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transaction_amount_TextInput.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                transaction_amount_TextInput.setEnabled(false);
            }
        });

        transaction_send_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String CONTEXT_ROOT = HttpConnectConstant.HTTP_SERVER_URL;
                        Address destination =
                                Address.fromBase58(WalletContextHolder.networkParameters, transaction_toAddress_TextInput.getText().toString());

                        Wallet wallet = WalletContextHolder.get().wallet();
                        wallet.setServerURL(CONTEXT_ROOT);

                        Coin amount = Coin.parseCoin(transaction_amount_TextInput.getText().toString(), Utils.HEX.decode(transaction_tokenname_Spinner.getSelectedItem().toString()));
                        long factor = 1;
                        amount = amount.multiply(factor);

                        try {
                            wallet.pay(WalletContextHolder.getAesKey(), destination, amount, "");
                            Toast.makeText(getActivity(), getString(R.string.wallet_transaction_success), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.wallet_transaction_fail), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
    }
}