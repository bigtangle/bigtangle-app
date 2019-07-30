package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.datepicker.CustomDatePicker;
import net.bigtangle.wallet.components.datepicker.DateFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

public class MarketOrderFragment extends BaseLazyFragment {

    @BindView(R.id.state_radio_group)
    RadioGroup statusRadioGroup;

    @BindView(R.id.address_spinner)
    Spinner addressSpinner;

    @BindView(R.id.token_spinner)
    Spinner passSpinner;

    @BindView(R.id.unit_price_text_input)
    TextInputEditText unitPriceInput;

    @BindView(R.id.amount_text_input)
    TextInputEditText numInput;

    @BindView(R.id.date_begin_input)
    TextView dateBeginInput;

    @BindView(R.id.date_end_input)
    TextView dateEndInput;

    @BindView(R.id.search_button)
    Button searchBtn;

    ArrayAdapter<String> tokenAdapter;
    ArrayAdapter<String> addressAdapter;

    private CustomDatePicker mTimerPicker;

    private boolean dateEndInputFlag;
    private boolean dateInputInputFlag;

    private String[] addressArray = {"1", "2", "3", "4"};
    private String[] tokenArray = {"1", "2", "3", "4"};

    public MarketOrderFragment() {
    }

    public static MarketOrderFragment newInstance() {
        MarketOrderFragment fragment = new MarketOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addressAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, addressArray);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tokenAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, tokenArray);
        tokenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void onLazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_order, container, false);
    }

    @Override
    public void initEvent() {

        initTimerPicker();
        passSpinner.setAdapter(tokenAdapter);
        passSpinner.setSelection(0);

        addressSpinner.setAdapter(addressAdapter);
        addressSpinner.setSelection(0);

        this.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
                new LovelyInfoDialog(getContext())
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle(getContext().getString(R.string.dialog_title_error))
                        .setMessage(getContext().getString(R.string.network_response_data_failed))
                        .show();
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
