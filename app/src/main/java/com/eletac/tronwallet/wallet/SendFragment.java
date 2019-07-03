package com.eletac.tronwallet.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.eletac.tronwallet.CaptureActivityPortrait;
import com.eletac.tronwallet.Price;
import com.eletac.tronwallet.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SendFragment extends Fragment {

    private Spinner mAssets_Spinner;
    private Button mSend_Button;
    private ImageButton mQR_Button;
    private EditText mTo_EditText;
    private EditText mAmount_EditText;
    private TextView mAvailableLabel_TextView;
    private TextView mAvailable_TextView;
    private TextView mAsset_TextView;
    private EditText mAmountFiat_EditText;
    private TextView mAvailableFiatLabel_TextView;
    private TextView mAvailableFiat_TextView;
    private TextView mAmountEqualFiat_TextView;

    private Price mPrice;

    private boolean mIsUpdatingAmount = false;

    private AccountUpdatedBroadcastReceiver mAccountUpdatedBroadcastReceiver;

    public SendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            String content = result.getContents();
            if(content != null) {
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

                String asset = "";
                String address = "";
                double amount = 0;

                boolean setAmount = false;

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // {“token”:“TRX”, “address”:“TJo2xFo14Rnx9vvMSm1kRTQhVHPW4KPQ76", “amount”: “12"}
                if(jsonObject != null) {
                    try {
                        asset = jsonObject.has("asset") ? jsonObject.getString("asset") : (jsonObject.has("token") ? jsonObject.getString("token") : "");
                        address = jsonObject.has("address") ? jsonObject.getString("address") : "";

                        amount = jsonObject.getDouble("amount");
                        setAmount = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    List<String> contentParts = Arrays.asList(content.split(":"));

                    switch (contentParts.size()) {
                        // address
                        case 1: {
                            address = content;
                            break;
                        }
                        // tron:address?amount=xx   or  address:amount
                        case 2: {

                            try {
                                if(contentParts.get(1).contains("?")) {
                                    List<String> parts = Arrays.asList(contentParts.get(1).split("\\?"));

                                    address = parts.isEmpty() ? "" : parts.get(0);

                                    String part2 = contentParts.get(1);
                                    if(part2.contains("amount=")) {
                                        List<String> amountParts = Arrays.asList(part2.split("amount="));
                                        amount = numberFormat.parse(amountParts.get(parts.size()-1)).doubleValue();
                                        setAmount = true;
                                    }
                                } else {
                                    address = contentParts.get(0);
                                    amount = numberFormat.parse(contentParts.get(1)).doubleValue();
                                    setAmount = true;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case 3: {
                            asset = contentParts.get(0);
                            address = contentParts.get(1);

                            try {
                                amount = numberFormat.parse(contentParts.get(2)).doubleValue();
                                setAmount = true;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        default:
                    }
                }
                mTo_EditText.setText(address);
                if (setAmount) {
                    mAmount_EditText.setText(String.valueOf(amount));
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static SendFragment newInstance() {
        SendFragment fragment = new SendFragment();
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
        return inflater.inflate(R.layout.fragment_send, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAssets_Spinner = view.findViewById(R.id.Send_assets_spinner);
        mTo_EditText = view.findViewById(R.id.Send_to_editText);
        mAmount_EditText = view.findViewById(R.id.Send_amount_editText);
        mAvailable_TextView = view.findViewById(R.id.Send_available_textView);
        mAvailableLabel_TextView = view.findViewById(R.id.Send_available_label_textView);
        mAsset_TextView = view.findViewById(R.id.Send_asset_textView);
        mSend_Button = view.findViewById(R.id.Send_send_button);
        mQR_Button = view.findViewById(R.id.Send_qr_button);
        mAmountFiat_EditText = view.findViewById(R.id.Send_fiat_value_editText);
        mAvailableFiat_TextView = view.findViewById(R.id.Send_max_fiat_textView);
        mAvailableFiatLabel_TextView = view.findViewById(R.id.Send_max_fiat_label_textView);
        mAmountEqualFiat_TextView = view.findViewById(R.id.Send_amount_fiat_equal_textView);

        updateAssetsSpinner();

        mAssets_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAmount_EditText.setEnabled(true);

                updateAvailableAmount();
                updateFiatPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAmount_EditText.setEnabled(false);
            }
        });

        View.OnClickListener setAmountToAvailableClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                try {
                    mAmount_EditText.setText(String.valueOf(numberFormat.parse(mAvailable_TextView.getText().toString()).doubleValue()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        mAvailable_TextView.setOnClickListener(setAmountToAvailableClickListener);
        mAvailableLabel_TextView.setOnClickListener(setAmountToAvailableClickListener);

        View.OnClickListener setFiatAmountToAvailableClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                try {
                    mAmountFiat_EditText.setText(String.valueOf(numberFormat.parse(mAvailableFiat_TextView.getText().toString()).doubleValue()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        mAvailableFiat_TextView.setOnClickListener(setFiatAmountToAvailableClickListener);
        mAvailableFiatLabel_TextView.setOnClickListener(setFiatAmountToAvailableClickListener);

        mSend_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mQR_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(SendFragment.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt(getString(R.string.scan_send_to_qr_code));
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            }
        });


        mAmount_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFiatPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAmountFiat_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAssetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateAvailableAmount() {
    }

    private void updateAssetAmount() {
        if(mIsUpdatingAmount) {
            return;
        }
        mIsUpdatingAmount = true;
        String fiatAmountText = mAmountFiat_EditText.getText().toString();

        if(!fiatAmountText.equals("")) {
        } else {
            mAmount_EditText.setText("");
        }
        mIsUpdatingAmount = false;
    }

    private void updateFiatPrice() {
        if(mIsUpdatingAmount) {
            return;
        }
        mIsUpdatingAmount = true;
        String assetAmountText = mAmount_EditText.getText().toString();

        if(!assetAmountText.equals("")) {
            double asset_amount = Double.parseDouble(assetAmountText);
        } else {
            mAmountFiat_EditText.setText("");
        }
        mIsUpdatingAmount = false;
    }

    private void updateAssetsSpinner() {
        int position = mAssets_Spinner.getSelectedItemPosition();
        ArrayAdapter<String> adapter = getAssetNamesArrayAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAssets_Spinner.setAdapter(adapter);
        mAssets_Spinner.setSelection(position);
    }

    private ArrayAdapter<String> getAssetNamesArrayAdapter() {
        ArrayAdapter<String> adapter = null;

        Context context = getContext();


        return adapter;
    }

    private class AccountUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //updateAssetsSpinner();
            updateAvailableAmount();
        }
    }
}
