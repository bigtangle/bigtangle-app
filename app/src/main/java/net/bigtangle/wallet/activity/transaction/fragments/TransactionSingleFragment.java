package net.bigtangle.wallet.activity.transaction.fragments;

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

import net.bigtangle.wallet.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionSingleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.pay_method_spinner)
    Spinner payMethodSpinner;
    @BindView(R.id.amount_text_input)
    TextInputEditText amountTextInput;
    @BindView(R.id.from_token_spinner)
    Spinner fromTokenSpinner;
    @BindView(R.id.to_token_spinner)
    Spinner toTokenSpinner;
    @BindView(R.id.use_text_input)
    TextInputEditText useTextInput;
    @BindView(R.id.pay_button)
    Button payButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;

    ArrayAdapter<String> fromTokenAdapter;
    ArrayAdapter<String> toTokenAdapter;
    ArrayAdapter<String> payMethodAdapter;

    private List<String> fromTokenNames;
    private List<String> toTokenNames;
    private String[] payMethodArray = {"支付", "多重签名支付", "多重地址支付", "多重签名地址支付"};

    public TransactionSingleFragment() {
    }

    public static TransactionSingleFragment newInstance() {
        TransactionSingleFragment fragment = new TransactionSingleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fromTokenNames = new ArrayList<>();
        this.toTokenNames = new ArrayList<>();
        Context context = getContext();
        fromTokenAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, fromTokenNames);
        fromTokenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toTokenAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, toTokenNames);
        toTokenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        payMethodAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, payMethodArray);
        payMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initData() {
//        List<String> keyStrHex = new ArrayList<String>();
//
//        Wallet wallet = WalletContextHolder.get().wallet();
//        for (ECKey ecKey : wallet.walletKeys(WalletContextHolder.getAesKey())) {
//            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
//        }
//        new HttpNetTaskRequest(getContext()).httpRequest(ReqCmd.getBalances, keyStrHex, new HttpNetComplete() {
//            @Override
//            public void completeCallback(String jsonStr) {
//                try {
//                    GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(jsonStr, GetBalancesResponse.class);
//                    fromTokenNames.clear();
//                    for (UTXO utxo : getBalancesResponse.getOutputs()) {
//                        Coin coin = utxo.getValue();
//                        if (coin.isZero()) {
//                            continue;
//                        }
//                        byte[] tokenid = coin.getTokenid();
//                        fromTokenNames.add(Utils.HEX.encode(tokenid));
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            fromTokenAdapter.notifyDataSetChanged();
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        for (int i = 0; i < 4; i++) {
            fromTokenNames.add("" + i);
            toTokenNames.add("" + i );
        }

        fromTokenAdapter.notifyDataSetChanged();
        toTokenAdapter.notifyDataSetChanged();

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
        initSpinner();
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo doPay
//               new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {
//                    @Override
//                    public void completeCallback(String jsonStr) {
//                        new LovelyInfoDialog(getContext())
//                                .setTopColorRes(R.color.colorPrimary)
//                                .setIcon(R.drawable.ic_info_white_24px)
//                                .setTitle("操作成功")
//                                .setMessage("钱包进行支付成功")
//                                .show();
//                        return;
//                    }
//                }, new HttpRunaExecute() {
//                    @Override
//                    public void execute() throws Exception {
//                        String CONTEXT_ROOT = HttpConnectConstant.HTTP_SERVER_URL;
//                        final String toAddress = mViewHolder.toAddressTextInput.getText().toString();
//                        final String amountValue = mViewHolder.amountTextInput.getText().toString();
//
//                        Address destination = Address.fromBase58(WalletContextHolder.networkParameters, toAddress);
//
//                        Wallet wallet = WalletContextHolder.get().wallet();
//                        wallet.setServerURL(CONTEXT_ROOT);
//
//                        byte[] tokenidBuf = Utils.HEX.decode(mViewHolder.tokenNameSpinner.getSelectedItem().toString());
//                        Coin amount = Coin.parseCoin(amountValue, tokenidBuf);
//
//                        long factor = 1;
//                        amount = amount.multiply(factor);
//                        wallet.pay(WalletContextHolder.getAesKey(), destination, amount, "");
//                    }
//                }).execute();
//                amountTextInput.setEnabled(false);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo cancel
            }
        });

        initData();
    }

    private void initSpinner() {
        fromTokenSpinner.setAdapter(fromTokenAdapter);
        fromTokenSpinner.setSelection(0);
        fromTokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amountTextInput.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                amountTextInput.setEnabled(false);
            }
        });


        toTokenSpinner.setAdapter(toTokenAdapter);
        toTokenSpinner.setSelection(0);
        toTokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amountTextInput.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                amountTextInput.setEnabled(false);
            }
        });


        payMethodSpinner.setAdapter(payMethodAdapter);
        payMethodSpinner.setSelection(0);
    }

    @Override
    public void onRefresh() {

    }
}