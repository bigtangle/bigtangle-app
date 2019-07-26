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
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Address;
import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.components.TokenItem;
import net.bigtangle.wallet.components.TokenItemListAdapter;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.exception.HttpNetExecuteException;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionPaymentFragment extends Fragment {

    @BindView(R.id.pay_method_spinner)
    Spinner payMethodSpinner;

    @BindView(R.id.amount_text_input)
    TextInputEditText amountTextInput;

    @BindView(R.id.token_spinner)
    Spinner tokenSpinner;

    @BindView(R.id.to_address_text_input)
    TextInputEditText toAddressTextInput;

    @BindView(R.id.memo_text_input)
    TextInputEditText memoTextInput;

    @BindView(R.id.pay_button)
    Button payButton;

    TokenItemListAdapter tokenAdapter;
    ArrayAdapter<String> payMethodAdapter;

    private List<TokenItem> tokenNames;
    private String[] payMethodArray = {"Pay", "Multi-signature Pay", "Multi-addresses Pay", "Multiple Signature Addresses Pay"};
    private boolean isInit = false;

    public static TransactionPaymentFragment newInstance() {
        return new TransactionPaymentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.tokenNames == null) {
            this.tokenNames = new ArrayList<TokenItem>();
        }
        this.payMethodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, payMethodArray);
        this.payMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.tokenAdapter = new TokenItemListAdapter(getContext(), tokenNames);
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
                        TokenItem tokenItem = new TokenItem();
                        tokenItem.setTokenId(Utils.HEX.encode(tokenid));

                        Token token = getBalancesResponse.getTokennames().get(tokenItem.getTokenId());
                        if (token != null) {
                            tokenItem.setTokenName(token.getTokenname());
                        } else {
                            tokenItem.setTokenName(tokenItem.getTokenId());
                        }
                        tokenNames.add(tokenItem);
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
        View view = inflater.inflate(R.layout.fragment_transaction_payment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {
                    @Override
                    public void completeCallback(String jsonStr) {
                        new LovelyInfoDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle(getContext().getString(R.string.dialog_title_info))
                                .setMessage(getContext().getString(R.string.wallet_payment_success))
                                .show();
                        return;
                    }
                }, new HttpRunaExecute() {
                    @Override
                    public void execute() throws Exception {
                        String CONTEXT_ROOT = HttpConnectConstant.HTTP_SERVER_URL;
                        final String toAddress = toAddressTextInput.getText().toString();
                        if (StringUtils.isBlank(toAddress)) {
                            throw new HttpNetExecuteException(getContext().getString(R.string.address_not_empty));
                        }
                        final String amountValue = amountTextInput.getText().toString();
                        if (StringUtils.isBlank(amountValue)) {
                            throw new HttpNetExecuteException(getContext().getString(R.string.amount_not_empty));
                        }
                        if (tokenSpinner.getSelectedItem() == null) {
                            throw new HttpNetExecuteException(getContext().getString(R.string.token_not_empty));
                        }
                        TextView tokenIdTextView = tokenSpinner.getSelectedView().findViewById(R.id.tokenIdTextView);
                        final String tokenValue = tokenIdTextView.getText().toString();
                        if (StringUtils.isBlank(tokenValue)) {
                            throw new HttpNetExecuteException(getContext().getString(R.string.token_not_empty));
                        }

                        Address destination = Address.fromBase58(WalletContextHolder.networkParameters, toAddress);

                        Wallet wallet = WalletContextHolder.get().wallet();
                        wallet.setServerURL(CONTEXT_ROOT);

                        byte[] tokenidBuf = Utils.HEX.decode(tokenValue);
                        Coin amount = Coin.parseCoin(amountValue, tokenidBuf);

                        long factor = 1;
                        amount = amount.multiply(factor);

                        final String memo = memoTextInput.getText().toString();
                        wallet.pay(WalletContextHolder.getAesKey(), destination, amount, memo);
                    }
                }).execute();
            }
        });
        if (this.isInit == false) {
            this.initData();
        }
        this.isInit = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (this.isInit) {
                this.initData();
            }
        }
    }

    private void initView() {
        tokenSpinner.setAdapter(tokenAdapter);
        tokenSpinner.setSelection(0);

        payMethodSpinner.setAdapter(payMethodAdapter);
        payMethodSpinner.setSelection(0);
    }
}