package com.eletac.tronwallet.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eletac.tronwallet.R;

public class FreezeFragment extends Fragment {

    private TextView mFrozenNow_TextView;
    private TextView mFrozenNew_TextView;
    private TextView mVotesNow_TextView;
    private TextView mVotesNew_TextView;
    private TextView mBandwidthNow_TextView;
    private TextView mBandwidthNew_TextView;
    private TextView mEnergyNow_TextView;
    private TextView mExpires_TextView;
    private TextView mEnergyWarning_TextView;
    private EditText mFreezeAmount_EditText;
    private SeekBar mFreezeAmount_SeekBar;
    private Button mFreeze_Button;
    private Button mUnfreeze_Button;
    private RadioButton mGainBandwidth_RadioButton;
    private RadioButton mGainEnergy_RadioButton;
    private RadioButton mUnfreezeBandwidth_RadioButton;
    private RadioButton mUnfreezeEnergy_RadioButton;

    private AccountUpdatedBroadcastReceiver mAccountUpdatedBroadcastReceiver;

    private long mFreezeAmount = 0;
    private boolean mUpdatingUI = false;

    public FreezeFragment() {
        // Required empty public constructor
    }

    public static FreezeFragment newInstance() {
        FreezeFragment fragment = new FreezeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountUpdatedBroadcastReceiver = new AccountUpdatedBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freeze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFrozenNow_TextView = view.findViewById(R.id.Freeze_frozen_now_textView);
        mFrozenNew_TextView = view.findViewById(R.id.Freeze_frozen_new_textView);
        mVotesNow_TextView = view.findViewById(R.id.Freeze_votes_now_textView);
        mVotesNew_TextView = view.findViewById(R.id.Freeze_votes_new_textView);
        mBandwidthNow_TextView = view.findViewById(R.id.Freeze_bandwidth_now_textView);
        mBandwidthNew_TextView = view.findViewById(R.id.Freeze_bandwidth_new_textView);
        mEnergyNow_TextView = view.findViewById(R.id.Freeze_energy_now_textView);
        mExpires_TextView = view.findViewById(R.id.Freeze_expire_textView);
        mEnergyWarning_TextView = view.findViewById(R.id.Freeze_energy_warning_textView);
        mFreezeAmount_EditText = view.findViewById(R.id.Freeze_amount_editText);
        mFreezeAmount_SeekBar = view.findViewById(R.id.Freeze_amount_seekBar);
        mFreeze_Button = view.findViewById(R.id.Freeze_button);
        mUnfreeze_Button= view.findViewById(R.id.Freeze_un_button);
        mGainBandwidth_RadioButton= view.findViewById(R.id.Freeze_bandwidth_radioButton);
        mGainEnergy_RadioButton= view.findViewById(R.id.Freeze_energy_radioButton);
        mUnfreezeBandwidth_RadioButton= view.findViewById(R.id.Freeze_un_bandwidth_radioButton);
        mUnfreezeEnergy_RadioButton= view.findViewById(R.id.Freeze_un_energy_radioButton);

        CompoundButton.OnCheckedChangeListener radio = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateUI();
            }
        };

        mGainBandwidth_RadioButton.setOnCheckedChangeListener(radio);
        mGainEnergy_RadioButton.setOnCheckedChangeListener(radio);
        mUnfreezeBandwidth_RadioButton.setOnCheckedChangeListener(radio);
        mUnfreezeEnergy_RadioButton.setOnCheckedChangeListener(radio);

        mFreezeAmount_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!mUpdatingUI) {
                    mUpdatingUI = true;
                    mFreezeAmount = mFreezeAmount_EditText.getText().length() > 0 ? Long.valueOf(mFreezeAmount_EditText.getText().toString()) : 0L;
                    if(Build.VERSION.SDK_INT >= 24) {
                        mFreezeAmount_SeekBar.setProgress((int) mFreezeAmount, true);
                    } else {
                        mFreezeAmount_SeekBar.setProgress((int) mFreezeAmount);
                    }
                    updateUI();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mFreezeAmount_SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!mUpdatingUI) {
                    mUpdatingUI = true;
                    mFreezeAmount = progress;
                    mFreezeAmount_EditText.setText(String.valueOf(mFreezeAmount));
                    updateUI();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mFreeze_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mUnfreeze_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mAccountUpdatedBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mAccountUpdatedBroadcastReceiver, new IntentFilter(AccountUpdater.ACCOUNT_UPDATED));
    }

    private void updateUI() {
        mUpdatingUI = false;
    }

    private class AccountUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }
}
