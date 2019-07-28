package net.bigtangle.wallet.activity.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
import net.bigtangle.wallet.activity.transaction.adapter.TokenItemListAdapter;
import net.bigtangle.wallet.activity.transaction.model.TokenItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
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

public class TransactionPaymentFragment extends BaseLazyFragment {

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
    private String[] payMethodArray;

    public static TransactionPaymentFragment newInstance() {
        return new TransactionPaymentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.tokenNames == null) {
            this.tokenNames = new ArrayList<TokenItem>();
        }
        if (this.payMethodArray == null) {
            payMethodArray = new String[]{getContext().getString(R.string.pay),
                    getContext().getString(R.string.multiple_signature_pay),
                    getContext().getString(R.string.multiple_addresses_pay),
                    getContext().getString(R.string.multiple_signature_addresses_pay)};
        }
        this.payMethodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, payMethodArray);
        this.payMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.tokenAdapter = new TokenItemListAdapter(getContext(), tokenNames);
    }

    @Override
    public void onLazyLoad() {
        List<String> keyStrHex = new ArrayList<String>();
        for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
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
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_transaction_payment, container, false);
        return view;
    }

    @Override
    public void initEvent() {
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
                        TextView tokenIdTextView = tokenSpinner.getSelectedView().findViewById(R.id.token_id_text_view);
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
                        wallet.pay(WalletContextHolder.get().getAesKey(), destination, amount, memo);
                    }
                }).execute();
            }
        });
    }

    private void initView() {
        tokenSpinner.setAdapter(tokenAdapter);
        tokenSpinner.setSelection(0);

        payMethodSpinner.setAdapter(payMethodAdapter);
        payMethodSpinner.setSelection(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}