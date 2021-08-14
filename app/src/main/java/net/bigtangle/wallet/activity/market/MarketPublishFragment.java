package net.bigtangle.wallet.activity.market;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Coin;
import net.bigtangle.core.Token;
import net.bigtangle.core.Utils;
import net.bigtangle.core.exception.InsufficientMoneyException;
import net.bigtangle.utils.MonetaryFormat;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.SPUtil;
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
import net.bigtangle.wallet.core.utils.CommonUtil;
import net.bigtangle.wallet.core.utils.DateTimeUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MarketPublishFragment extends BaseLazyFragment {

    @BindView(R.id.state_radio_group)
    RadioGroup statusRadioGroup;

    @BindView(R.id.token_spinner)
    Spinner tokenSpinner;

    @BindView(R.id.unit_price_text_input)
    TextInputEditText unitPriceInput;

    @BindView(R.id.amount_text_input)
    TextInputEditText amountTextInput;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.start_date_text_view)
    TextView startDateTextView;

    @BindView(R.id.end_date_text_view)
    TextView endDateTextView;

    @BindView(R.id.basetoken_spinner)
    Spinner basetokenSpinner;

    TokenItemListAdapter basetokenAdapter;

    TokenItemListAdapter tokenAdapter;

    private CustomDatePicker mTimerPicker;

    private boolean dateStartInputFlag;
    private boolean dateEndInputFlag;

    private List<TokenItem> tokenItemList;
    private boolean flag = false;


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
        setFroceLoadData(true);
        this.tokenAdapter = new TokenItemListAdapter(getContext(), tokenItemList);
        List<TokenItem> basetokenItemList = new ArrayList<TokenItem>();
        TokenItem yuan = new TokenItem();
        yuan.setTokenId("03bed6e75294e48556d8bb2a53caf6f940b70df95760ee4c9772681bbf90df85ba");
        yuan.setTokenName("人民币@bigtangle");
        basetokenItemList.add(yuan);
        TokenItem bc = new TokenItem();
        bc.setTokenId("bc");
        bc.setTokenName("BIG");
        basetokenItemList.add(bc);
        this.basetokenAdapter = new TokenItemListAdapter(getContext(), basetokenItemList);
    }

    @Override
    public void onLazyLoad() {
        initData();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_publish, container, false);
    }

    @Override
    public void initEvent() {

        initTimerPicker();
        this.tokenSpinner.setAdapter(tokenAdapter);
        this.tokenSpinner.setSelection(0);
        this.basetokenSpinner.setAdapter(basetokenAdapter);
        this.basetokenSpinner.setSelection(0);
        // unitPriceInput.setText("50");

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {
                    @Override
                    public void completeCallback(byte[] jsonStr) {
                        new LovelyInfoDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle(getContext().getString(R.string.dialog_title_info))
                                .setMessage(getContext().getString(R.string.successful_order_release))
                                .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                cleanInputContent();
                            }
                        });
                    }
                }, new HttpRunaExecute() {
                    @Override
                    public void execute() throws Exception {
                        String un = SPUtil.get(getContext(), "username", "").toString();
                        InputStream stream = CommonUtil.loadFromDB(un, getContext());
                        WalletContextHolder.loadWallet(stream);

                        WalletContextHolder.wallet.setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
                        if (tokenSpinner.getSelectedItem() == null) {
                            throw new ToastException(getContext().getString(R.string.token_not_empty));
                        }
                        TextView tokenIdTextView = tokenSpinner.getSelectedView().findViewById(R.id.token_id_text_view);
                        final String tokenValue = tokenIdTextView.getText().toString();
                        if (StringUtils.isBlank(tokenValue)) {
                            throw new ToastException(getContext().getString(R.string.token_not_empty));
                        }
                        String tokenid = tokenValue;
                        String basetokenValue = ((TokenItem) basetokenSpinner.getSelectedItem()).getTokenId();
                        Integer priceshift = WalletContextHolder.networkParameters.getOrderPriceShift(basetokenValue);

                        boolean isBuy_ = true;
                        for (int i = 0; i < statusRadioGroup.getChildCount(); i++) {
                            RadioButton radioButton = (RadioButton) statusRadioGroup.getChildAt(i);
                            if (radioButton.isChecked()) {
                                isBuy_ = radioButton.getText().equals(getContext().getString(R.string.buy));
                                break;
                            }
                        }
                        String typeStr = isBuy_ ? "buy" : "sell";
                        if (StringUtils.isBlank(amountTextInput.getText().toString())) {
                            throw new ToastException(getContext().getString(R.string.amount_not_empty));
                        }

                        Token t = WalletContextHolder.wallet.checkTokenId(tokenid);
                        Coin quantity = MonetaryFormat.FIAT.noCode().parse(amountTextInput.getText().toString(), Utils.HEX.decode(tokenid),
                                t.getDecimals());
                        if (quantity.getValue().signum() <= 0) {
                            throw new ToastException(getContext().getString(R.string.insufficient_amount));
                        }

                        if (StringUtils.isBlank(unitPriceInput.getText().toString())) {
                            throw new ToastException(getContext().getString(R.string.unit_price_not_empty));
                        }
                        Token base = WalletContextHolder.wallet.checkTokenId(basetokenValue);

                        Coin price = MonetaryFormat.FIAT.noCode().parse(unitPriceInput.getText().toString(),
                                Utils.HEX.decode(basetokenValue), base.getDecimals() + priceshift);

                        if (price.getValue().signum() <= 0) {
                            throw new ToastException(getContext().getString(R.string.insufficient_price));
                        }

                        if (startDateTextView.getText() == null) {
                            throw new ToastException(getContext().getString(R.string.start_date_not_empty));
                        }

                        String dateBeginStr = startDateTextView.getText().toString();
                        if (StringUtils.isBlank(dateBeginStr)) {
                            throw new ToastException(getContext().getString(R.string.start_date_not_empty));
                        }
                        long dateBeginLong = DateTimeUtils.toDateMillis(dateBeginStr + ":00");
                        if (endDateTextView.getText() == null) {
                            throw new ToastException(getContext().getString(R.string.end_date_not_empty));
                        }
                        String dateEndStr = endDateTextView.getText().toString();
                        if (StringUtils.isBlank(dateEndStr)) {
                            throw new ToastException(getContext().getString(R.string.end_date_not_empty));
                        }
                        long dateEndLong = DateTimeUtils.toDateMillis(dateEndStr + ":00");
                        if (dateEndLong < dateBeginLong) {
                            dateEndLong = dateBeginLong;
                        }
                        String priceTemp = unitPriceInput.getText().toString();
                         BigDecimal lastPrice = WalletContextHolder.wallet.getLastPrice(tokenid, basetokenValue);
                        if (!flag)
                            if (new BigDecimal(priceTemp).compareTo(lastPrice.multiply(new BigDecimal("1.3"))) == 1
                                    || new BigDecimal(priceTemp).compareTo(lastPrice.multiply(new BigDecimal("0.7"))) == -1) {

                                flag = true;
                                throw new ToastException(getContext().getString(R.string.lastPrice) + lastPrice.toString() + "," + getContext().getString(R.string.price_warn));

                            }


                        try {
                            WalletContextHolder.wallet.setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
                            if (typeStr.equals("sell")) {
                                WalletContextHolder.wallet.sellOrder(WalletContextHolder.getAesKey(), tokenid, price.getValue().longValue(), quantity.getValue().longValue(),
                                        dateEndLong, dateBeginLong, basetokenValue, true);
                            } else {
                                WalletContextHolder.wallet.buyOrder(WalletContextHolder.getAesKey(), tokenid, price.getValue().longValue(), quantity.getValue().longValue(),
                                        dateEndLong, dateBeginLong, basetokenValue, true);
                            }

                            amountTextInput.setText("");
                        } catch (InsufficientMoneyException e) {
                            throw new ToastException(getContext().getString(R.string.insufficient_amount));
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

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateStartInputFlag = true;
                mTimerPicker.show(startDateTextView.getText().toString());
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateEndInputFlag = true;
                mTimerPicker.show(endDateTextView.getText().toString());
            }
        });
    }

    public void initData() {
        boolean isBuy_ = false;
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
            public void completeCallback(byte[] jsonStr) {
            }
        }, new HttpRunaExecute() {

            @Override
            public void execute() throws Exception {
                tokenItemList.clear();
                if (!isBuy) {
                    for (TokenItem tokenItem : HttpService.getTokensItemList()) {

                        tokenItemList.add(tokenItem);

                    }
                } else {
                    for (TokenItem tokenItem : HttpService.getTokensItemList()) {
                        tokenItemList.add(tokenItem);
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
    }

    private void cleanInputContent() {
        amountTextInput.setText("");
    }

    private void initTimerPicker() {

        String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(8), true);

        startDateTextView.setText(beginTime);
        endDateTextView.setText(endTime);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(getContext(), new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                if (dateEndInputFlag) {
                    endDateTextView.setText(DateFormatUtils.long2Str(timestamp, true));
                }
                if (dateStartInputFlag) {
                    startDateTextView.setText(DateFormatUtils.long2Str(timestamp, true));
                    endDateTextView.setText(DateFormatUtils.long2Str(timestamp
                            + TimeUnit.HOURS.toMillis(6), true));
                }
                dateStartInputFlag = false;
                dateEndInputFlag = false;
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
}
