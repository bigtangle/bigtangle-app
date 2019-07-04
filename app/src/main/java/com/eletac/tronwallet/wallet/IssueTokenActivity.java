package com.eletac.tronwallet.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.bigtangle.wallet.components.InputFilterMinMax;
import com.eletac.tronwallet.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IssueTokenActivity extends AppCompatActivity {

    private EditText mName_EditText;
    private EditText mAbbr_EditText;
    private EditText mSupply_EditText;
    private EditText mURL_EditText;
    private EditText mDesc_EditText;

    private EditText mExchangeTrxAmount_EditText;
    private EditText mExchangeTokenAmount_EditText;
    private TextView mTokenPrice_TextView;

    private EditText mFrozenAmount_EditText;
    private EditText mFrozenDays_EditText;

    private EditText mTotalBandwidth_EditText;
    private EditText mBandwidthPerAccount_EditText;

    private Button mSetStart_Button;
    private Button mSetEnd_Button;
    private TextView mStart_TextView;
    private TextView mEnd_TextView;

    private Button mCreate_Button;

    private int mStartYear, mStartMonth, mStartDay;
    private int mStartHour, mStartMinute, mStartSecond;

    private int mEndYear, mEndMonth, mEndDay;
    private int mEndHour, mEndMinute, mEndSecond;

    private boolean mIsStartSet = false, mIsEndSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_token);

        mName_EditText = findViewById(R.id.IssueToken_name_editText);
        mAbbr_EditText = findViewById(R.id.IssueToken_abbr_editText);
        mSupply_EditText = findViewById(R.id.IssueToken_supply_editText);
        mURL_EditText = findViewById(R.id.IssueToken_url_editText);
        mDesc_EditText = findViewById(R.id.IssueToken_desc_editText);
        mExchangeTrxAmount_EditText = findViewById(R.id.IssueToken_trx_amount_editText);
        mExchangeTokenAmount_EditText = findViewById(R.id.IssueToken_token_amount_editText);
        mTokenPrice_TextView = findViewById(R.id.IssueToken_price_textView);
        mFrozenAmount_EditText = findViewById(R.id.IssueToken_frozen_amount_editText);
        mFrozenDays_EditText = findViewById(R.id.IssueToken_frozen_days_editText);
        mTotalBandwidth_EditText = findViewById(R.id.IssueToken_total_bandwidth_editText);
        mBandwidthPerAccount_EditText = findViewById(R.id.IssueToken_bandwidth_per_account_editText);
        mSetStart_Button = findViewById(R.id.IssueToken_set_start_button);
        mSetEnd_Button = findViewById(R.id.IssueToken_set_end_button);
        mStart_TextView = findViewById(R.id.IssueToken_start_time_textView);
        mEnd_TextView = findViewById(R.id.IssueToken_end_time_textView);
        mCreate_Button = findViewById(R.id.IssueToken_create_button);

        mSupply_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Long.MAX_VALUE)});
        mExchangeTrxAmount_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Integer.MAX_VALUE)});
        mExchangeTokenAmount_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Integer.MAX_VALUE)});
        mFrozenAmount_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Long.MAX_VALUE)});
        mFrozenDays_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Long.MAX_VALUE)});
        mTotalBandwidth_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Long.MAX_VALUE)});
        mBandwidthPerAccount_EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, Long.MAX_VALUE)});

        mTokenPrice_TextView.setText("-");

        mSetStart_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                mStartYear = year;
                                                mStartMonth = monthOfYear;
                                                mStartDay = dayOfMonth;
                                                mStartHour = hourOfDay;
                                                mStartMinute = minute;
                                                mStartSecond = second;

                                                DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
                                                mStart_TextView.setText(dateTimeInstance.format(new Date(startDateTimeToMillis())));

                                                mIsStartSet = true;
                                            }
                                        },
                                        true
                                );
                                tpd.show(getFragmentManager(), "timepickerdialog");
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "datepickerdialog");
            }
        });

        mSetEnd_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                mEndYear = year;
                                                mEndMonth = monthOfYear;
                                                mEndDay = dayOfMonth;
                                                mEndHour = hourOfDay;
                                                mEndMinute = minute;
                                                mEndSecond = second;

                                                DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
                                                mEnd_TextView.setText(dateTimeInstance.format(new Date(endDateTimeToMillis())));

                                                mIsEndSet = true;
                                            }
                                        },
                                        true
                                );
                                tpd.show(getFragmentManager(), "timepickerdialog");
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "datepickerdialog");
            }
        });

        mCreate_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        TextWatcher exchangeTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int trxAmount = 0;
                int tokenAmount = 0;

                try {
                    trxAmount = Integer.parseInt(mExchangeTrxAmount_EditText.getText().toString());
                    tokenAmount = Integer.parseInt(mExchangeTokenAmount_EditText.getText().toString());
                } catch (NumberFormatException ignored) {
                }

                if (tokenAmount != 0) {
                    double price = trxAmount / (double) tokenAmount;
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    numberFormat.setMaximumFractionDigits(6);
                    mTokenPrice_TextView.setText(numberFormat.format(price));
                } else {
                    mTokenPrice_TextView.setText("-");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mExchangeTrxAmount_EditText.addTextChangedListener(exchangeTextWatcher);
        mExchangeTokenAmount_EditText.addTextChangedListener(exchangeTextWatcher);
    }

    private long startDateTimeToMillis() {
        Calendar dateTime = Calendar.getInstance();
        dateTime.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute, mStartSecond);
        return dateTime.getTimeInMillis();
    }

    private long endDateTimeToMillis() {
        Calendar dateTime = Calendar.getInstance();
        dateTime.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute, mEndSecond);
        return dateTime.getTimeInMillis();
    }
}
