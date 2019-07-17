package net.bigtangle.wallet.activity.transaction.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionSingleFragment extends Fragment {

    @BindView(R.id.pay_method_spinner)
    Spinner payMethodSpinner;

    @BindView(R.id.amount_text_input)
    TextInputEditText amountTextInput;

    @BindView(R.id.token_spinner)
    Spinner tokenSpinner;

    @BindView(R.id.to_address_text_input)
    TextInputEditText toAddressTextInput;

    @BindView(R.id.use_text_input)
    TextInputEditText useTextInput;

    @BindView(R.id.pay_button)
    Button payButton;

    ArrayAdapter<String> tokenAdapter;
    ArrayAdapter<String> payMethodAdapter;

    private List<String> tokenNames;
    private String[] payMethodArray = {"支付", "多重签名支付", "多重地址支付", "多重签名地址支付"};

    public static TransactionSingleFragment newInstance() {
        TransactionSingleFragment fragment = new TransactionSingleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.tokenNames == null) {
            this.tokenNames = new ArrayList<String>();
        }
        payMethodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, payMethodArray);
        payMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tokenAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, tokenNames);
        tokenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initData() {
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
                            tokenAdapter.notifyDataSetChanged();
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
        View mContextView = inflater.inflate(R.layout.fragment_transaction_single, container, false);
        ButterKnife.bind(this, mContextView);
        return mContextView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo doPay
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
                        final String toAddress = toAddressTextInput.getText().toString();
                        final String amountValue = amountTextInput.getText().toString();

                        Address destination = Address.fromBase58(WalletContextHolder.networkParameters, toAddress);

                        Wallet wallet = WalletContextHolder.get().wallet();
                        wallet.setServerURL(CONTEXT_ROOT);

                        byte[] tokenidBuf = Utils.HEX.decode(tokenSpinner.getSelectedItem().toString());
                        Coin amount = Coin.parseCoin(amountValue, tokenidBuf);

                        long factor = 1;
                        amount = amount.multiply(factor);

                        final String memo = useTextInput.getText().toString();
                        wallet.pay(WalletContextHolder.getAesKey(), destination, amount, memo);
                    }
                }).execute();
            }
        });

        initData();
    }

    private void initView() {
        tokenSpinner.setAdapter(tokenAdapter);
        tokenSpinner.setSelection(0);

        payMethodSpinner.setAdapter(payMethodAdapter);
        payMethodSpinner.setSelection(0);
    }
}