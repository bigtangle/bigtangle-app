package net.bigtangle.wallet.activity.transaction;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
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
import net.bigtangle.core.Contact;
import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.activity.settings.dialog.ContactAddDialog;
import net.bigtangle.wallet.activity.transaction.adapter.TokenItemListAdapter;
import net.bigtangle.wallet.activity.transaction.dialog.ContactChooseDialog;
import net.bigtangle.wallet.activity.transaction.model.TokenItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.exception.ToastException;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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

    @BindView(R.id.contact_button)
    Button contactButton;

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
        setFroceLoadData(true);
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
                            tokenItem.setTokenName(token.getTokennameDisplay());
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


                        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                ContactInfo contactInfo = (ContactInfo) HttpService.getUserdata(DataClassName.CONTACTINFO.name());
                                List<Contact> list = contactInfo.getContactList();
                                boolean find = false;
                                for (Contact contact : list) {
                                    if (contact.getAddress().equals(toAddressTextInput.getText().toString())) {
                                        find = true;
                                        break;
                                    }
                                }
                                return find;
                            }
                        });
                        // 启动线程请求当前应用程序版本号
                        new Thread(futureTask).start();
                        // 处理网络请求后的appNetInfo
                        boolean find = true;
                        try {
                            find = futureTask.get();
                            Log.i(LogConstant.TAG, "dig");
                        } catch (Exception e) {
                        }

                        if (!find) {
                            new ContactAddDialog(getActivity(), R.style.CustomDialogStyle)
                                    .setAddress(toAddressTextInput.getText().toString())
                                    .show();
                        } else {
                            new LovelyInfoDialog(getContext())
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_info_white_24px)
                                    .setTitle(getContext().getString(R.string.dialog_title_info))
                                    .setMessage(getContext().getString(R.string.wallet_payment_success))
                                    .show();
                        }

                    }
                }, new HttpRunaExecute() {
                    @Override
                    public void execute() throws Exception {
                        String CONTEXT_ROOT = HttpConnectConstant.HTTP_SERVER_URL;
                        final String toAddress = toAddressTextInput.getText().toString();
                        if (StringUtils.isBlank(toAddress)) {
                            throw new ToastException(getContext().getString(R.string.address_not_empty));
                        }
                        final String amountValue = amountTextInput.getText().toString();
                        if (StringUtils.isBlank(amountValue)) {
                            throw new ToastException(getContext().getString(R.string.amount_not_empty));
                        }
                        if (tokenSpinner.getSelectedItem() == null) {
                            throw new ToastException(getContext().getString(R.string.token_not_empty));
                        }
                        TextView tokenIdTextView = tokenSpinner.getSelectedView().findViewById(R.id.token_id_text_view);
                        final String tokenValue = tokenIdTextView.getText().toString();
                        if (StringUtils.isBlank(tokenValue)) {
                            throw new ToastException(getContext().getString(R.string.token_not_empty));
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

        this.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactChooseDialog(getContext(), R.style.CustomDialogStyle).setListener(new ContactChooseDialog.OnContactChooseItemListener() {
                    @Override
                    public void chooseAddress(String address) {
                        toAddressTextInput.setText(address);
                    }
                }).show();
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