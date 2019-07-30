package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Address;
import net.bigtangle.core.Coin;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.transaction.adapter.TokenItemListAdapter;
import net.bigtangle.wallet.activity.transaction.model.TokenItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.datepicker.CustomDatePicker;
import net.bigtangle.wallet.components.datepicker.DateFormatUtils;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.exception.ToastException;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;
import net.bigtangle.wallet.core.utils.CoinbaseUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

public class MarketPublishFragment extends BaseLazyFragment {

    @BindView(R.id.state_radio_group)
    RadioGroup statusRadioGroup;

    @BindView(R.id.address_spinner)
    Spinner addressSpinner;

    @BindView(R.id.token_spinner)
    Spinner tokenSpinner;

    @BindView(R.id.unit_price_text_input)
    TextInputEditText unitPriceInput;

    @BindView(R.id.amount_text_input)
    TextInputEditText amountTextInput;

    @BindView(R.id.search_button)
    Button searchButton;

    @BindView(R.id.date_begin_input)
    TextView dateBeginInput;

    @BindView(R.id.date_end_input)
    TextView dateEndInput;

    TokenItemListAdapter tokenAdapter;
    ArrayAdapter<String> addressAdapter;

    private CustomDatePicker mTimerPicker;

    private boolean dateEndInputFlag;
    private boolean dateInputInputFlag;

    private List<String> addressList;
    private List<TokenItem> tokenItemList;

    public static MarketPublishFragment newInstance() {
        MarketPublishFragment fragment = new MarketPublishFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.tokenItemList == null) {
            this.tokenItemList = new ArrayList<TokenItem>();
        }
        if (this.addressList == null) {
            this.addressList = new ArrayList<String>();
        }
        this.addressAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, addressList);
        this.addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.tokenAdapter = new TokenItemListAdapter(getContext(), tokenItemList);
    }

    @Override
    public void onLazyLoad() {
        initData();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_order, container, false);
    }

    @Override
    public void initEvent() {

        initTimerPicker();
        this.tokenSpinner.setAdapter(tokenAdapter);
        this.tokenSpinner.setSelection(0);

        this.addressSpinner.setAdapter(addressAdapter);
        this.addressSpinner.setSelection(0);

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        if (tokenSpinner.getSelectedItem() == null) {
                            throw new ToastException(getContext().getString(R.string.token_not_empty));
                        }
                        TextView tokenIdTextView = tokenSpinner.getSelectedView().findViewById(R.id.token_id_text_view);
                        final String tokenValue = tokenIdTextView.getText().toString();
                        if (StringUtils.isBlank(tokenValue)) {
                            throw new ToastException(getContext().getString(R.string.token_not_empty));
                        }
                        String tokenid = tokenValue;

                        boolean isBuy_ = true;
                        for (int i = 0; i < statusRadioGroup.getChildCount(); i++) {
                            RadioButton radioButton = (RadioButton) statusRadioGroup.getChildAt(i);
                            if (radioButton.isChecked()) {
                                isBuy_ = radioButton.getText().equals(getContext().getString(R.string.buy));
                                break;
                            }
                        }
                        String typeStr = isBuy_ ? "buy" : "sell";

                        if (addressSpinner.getSelectedItem() == null) {
                            throw new ToastException("地址不可以为空");
                        }
                        String address = addressSpinner.getSelectedItem().toString();
                        if (StringUtils.isBlank(address)) {
                            throw new ToastException("地址不可以为空");
                        }
                        byte[] pubKeyHash = Address.fromBase58(WalletContextHolder.networkParameters, address).getHash160();

                        Coin coin = CoinbaseUtil.calculateTotalUTXOList(pubKeyHash,
                                typeStr.equals("sell") ? tokenid : NetworkParameters.BIGTANGLE_TOKENID_STRING);

                        if (amountTextInput.getText() == null) {
                            throw new ToastException("地址不可以为空");
                        }
                        if (StringUtils.isBlank(amountTextInput.getText().toString())) {
                            throw new ToastException("地址不可以为空");
                        }
                        long amount = Long.valueOf(amountTextInput.getText().toString());
                        if (amount <= 0) {
                            throw new ToastException("地址不可以为空");
                        }

                        if (unitPriceInput.getText() == null) {
                            throw new ToastException("地址不可以为空");
                        }
                        if (StringUtils.isBlank(unitPriceInput.getText().toString())) {
                            throw new ToastException("地址不可以为空");
                        }
                        Coin price = Coin.parseCoin(unitPriceInput.getText().toString(), NetworkParameters.BIGTANGLE_TOKENID);
                        if (price.getValue() <= 0) {
                            throw new ToastException("地址不可以为空");
                        }

                        if (!typeStr.equals("sell")) {
                            amount = amount * price.getValue();
                        }

                        if (coin.getValue() < amount) {
                            throw new ToastException(" 余额不足");
                        }

                        WalletContextHolder.get().wallet().setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
                        if (typeStr.equals("sell")) {
                            WalletContextHolder.get().wallet().sellOrder(WalletContextHolder.get().getAesKey(), tokenid, price.getValue(), amount,
                                    System.currentTimeMillis(), System.currentTimeMillis());
                        } else {
                            WalletContextHolder.get().wallet().buyOrder(WalletContextHolder.get().getAesKey(), tokenid, price.getValue(), amount,
                                    System.currentTimeMillis(), System.currentTimeMillis());
                        }
                    }
                }).execute();
            }
        });

        this.statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initData();
            }
        });

        dateBeginInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateInputInputFlag = true;
                mTimerPicker.show(dateBeginInput.getText().toString());
            }
        });

        dateEndInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateEndInputFlag = true;
                mTimerPicker.show(dateEndInput.getText().toString());
            }
        });
    }

    public void initData() {
        boolean isBuy_ = true;
        for (int i = 0; i < statusRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) statusRadioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                isBuy_ = radioButton.getText().equals(getContext().getString(R.string.buy));
                break;
            }
        }
        final boolean isBuy = isBuy_;

        new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {

            @Override
            public void completeCallback(String jsonStr) {
            }
        }, new HttpRunaExecute() {

            @Override
            public void execute() throws Exception {
                tokenItemList.clear();
                if (!isBuy) {
                    for (TokenItem tokenItem : HttpService.getValidTokenItemList()) {
                        if (!isSystemCoin(tokenItem.getTokenId())) {
                            tokenItemList.add(tokenItem);
                        }
                    }
                } else {
                    for (TokenItem tokenItem : HttpService.getTokensItemList()) {
                        if (!isSystemCoin(tokenItem.getTokenId())) {
                            tokenItemList.add(tokenItem);
                        }
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tokenAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).execute();

        new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {

            }
        }, new HttpRunaExecute() {

            @Override
            public void execute() throws Exception {
                addressList.clear();
                if (!isBuy) {
                    if (tokenSpinner.getSelectedItem() != null) {
                        TextView tokenIdTextView = tokenSpinner.getSelectedView().findViewById(R.id.token_id_text_view);
                        final String tokenValue = tokenIdTextView.getText().toString();
                        if (!StringUtils.isBlank(tokenValue)) {
                            HashMap<String, Set<String>> validToken = HttpService.getValidTokenAddressResult();
                            if (validToken.get(tokenValue) != null && !validToken.get(tokenValue).isEmpty()) {
                                addressList.addAll(validToken.get(tokenValue));
                            }
                        }
                    }
                } else {
                    addressList.addAll(HttpService.getValidAddressSet());
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addressAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).execute();
    }

    public boolean isSystemCoin(String tokenId) {
        return ("BIG:" + NetworkParameters.BIGTANGLE_TOKENID_STRING).equals(tokenId)
                || (NetworkParameters.BIGTANGLE_TOKENID_STRING).equals(tokenId);
    }

    private void initTimerPicker() {

        String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = getAddYear(30);

        dateBeginInput.setText(beginTime);
        dateEndInput.setText(beginTime);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(getContext(), new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                if (dateEndInputFlag) {
                    dateEndInput.setText(DateFormatUtils.long2Str(timestamp, true));
                } else if (dateInputInputFlag) {
                    dateBeginInput.setText(DateFormatUtils.long2Str(timestamp, true));
                }
                dateEndInputFlag = false;
                dateInputInputFlag = false;
            }
        }, beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }

    public static String getAddYear(int addyear) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, addyear);
        Date date = cal.getTime();
        String year = dateFormat.format(date);
        return year;
    }
}
