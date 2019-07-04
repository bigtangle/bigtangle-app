package com.eletac.tronwallet.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.eletac.tronwallet.CaptureActivityPortrait;
import com.eletac.tronwallet.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.activity.MainActivity;

public class ImportWalletActivity extends AppCompatActivity {

    private Switch mAddressOnly_Switch;
    private Switch mRecoveryPhrase_Switch;

    private TextInputLayout mPassword_Layout;
    private TextInputLayout mPublicAddress_Layout;
    private TextInputLayout mPrivateKey_Layout;

    private EditText mName_EditText;
    private EditText mPassword_EditText;
    private EditText mPublicAddress_EditText;
    private EditText mPrivateKey_EditText;

    private Switch mColdWallet_Switch;
    private CheckBox mRisks_CheckBox;

    private Button mImport_Button;
    private Button mCreateWallet_Button;
    private ImageButton mPublicAddressQR_Button;
    private ImageButton mPrivateKeyQR_Button;

    private TextView mPasswordInfo_TextView;
    private TextView mPrivateKeyInfo_TextView;

    private static final int ADDRESS_REQUEST_CODE = 7541;
    private static final int PRIV_KEY_REQUEST_CODE = 9554;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                switch (requestCode) {
                    case ADDRESS_REQUEST_CODE:
                        mPublicAddress_EditText.setText(result.getContents());
                        break;
                    case PRIV_KEY_REQUEST_CODE:
                        mPrivateKey_EditText.setText(result.getContents());
                        break;

                        default:
                            super.onActivityResult(requestCode, resultCode, data);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);

        mPublicAddress_Layout = findViewById(R.id.ImportWallet_pub_address_textInputLayout);
        mPassword_Layout = findViewById(R.id.ImportWallet_password_textInputLayout);
        mPrivateKey_Layout = findViewById(R.id.ImportWallet_priv_key_textInputLayout);

        mName_EditText = findViewById(R.id.ImportWallet_name_editText);
        mPublicAddress_EditText = findViewById(R.id.ImportWallet_pub_address_editText);
        mPassword_EditText = findViewById(R.id.ImportWallet_password_editText);
        mPrivateKey_EditText = findViewById(R.id.ImportWallet_priv_key_editText);

        mColdWallet_Switch = findViewById(R.id.ImportWallet_cold_wallet_switch);
        mRisks_CheckBox = findViewById(R.id.ImportWallet_risks_checkbox);

        mImport_Button = findViewById(R.id.ImportWallet_import_button);
        mCreateWallet_Button = findViewById(R.id.ImportWallet_create_wallet_button);
        mPublicAddressQR_Button = findViewById(R.id.ImportWallet_pub_address_qr_button);
        mPrivateKeyQR_Button = findViewById(R.id.ImportWallet_priv_key_qr_button);

        mPasswordInfo_TextView = findViewById(R.id.ImportWallet_password_info_textView);
        mPrivateKeyInfo_TextView = findViewById(R.id.ImportWallet_priv_key_info_textView);

        mAddressOnly_Switch = findViewById(R.id.ImportWallet_address_switch);
        mRecoveryPhrase_Switch = findViewById(R.id.ImportWallet_recovery_phrase_switch);


        mCreateWallet_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateWalletActivity();
            }
        });

        mImport_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddressOnly_Switch.isChecked()) {
                    importPublicAddress();
                } else {
                    importPrivateKey();
                }
            }
        });

        mAddressOnly_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPublicAddress_Layout.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                mPassword_Layout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                mPasswordInfo_TextView.setVisibility(isChecked ? View.GONE : View.VISIBLE);

                mRecoveryPhrase_Switch.setVisibility(isChecked ? View.GONE : View.VISIBLE);

                mPrivateKey_Layout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                mPrivateKeyInfo_TextView.setVisibility(isChecked ? View.GONE : View.VISIBLE);

                mColdWallet_Switch.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                mRisks_CheckBox.setVisibility(isChecked ? View.GONE : View.VISIBLE);

                mImport_Button.setText(isChecked ? R.string.import_text : R.string.import_wallet);

                mPrivateKeyQR_Button.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                mPublicAddressQR_Button.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        mRecoveryPhrase_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrivateKey_Layout.setHint(isChecked ? getString(R.string.recovery_phrase) : getString(R.string.private_key));
                mPrivateKeyInfo_TextView.setText(isChecked ? R.string.recovery_phrase_info : R.string.private_key_info);
            }
        });

        mPublicAddressQR_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ImportWalletActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt(getString(R.string.scan_your_address_qr_code));
                integrator.setCameraId(0);
                integrator.setRequestCode(ADDRESS_REQUEST_CODE);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            }
        });

        mPrivateKeyQR_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ImportWalletActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt(getString(R.string.scan_your_priv_key_qr_code));
                integrator.setCameraId(0);
                integrator.setRequestCode(PRIV_KEY_REQUEST_CODE);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            }
        });

        mColdWallet_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    new LovelyInfoDialog(ImportWalletActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_info_white_24px)
                            .setTitle(getString(R.string.cold_wallet))
                            .setMessage(
                                    R.string.cold_wallet_info_dialog)
                            .show();
                }
            }
        });
    }

    private void importPublicAddress() {
    }

    private void importPrivateKey() {
    }

    private void startMainActivity() {
        Intent intent = new Intent(ImportWalletActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startCreateWalletActivity() {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        startActivity(intent);
        finish();
    }
}
