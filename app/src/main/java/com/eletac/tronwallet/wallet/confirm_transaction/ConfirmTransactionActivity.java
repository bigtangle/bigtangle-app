package com.eletac.tronwallet.wallet.confirm_transaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.eletac.tronwallet.R;
import com.eletac.tronwallet.block_explorer.contract.ContractLoaderFragment;
import com.eletac.tronwallet.wallet.SendReceiveActivity;
import com.eletac.tronwallet.wallet.SignTransactionActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.spongycastle.util.encoders.DecoderException;

import java.text.NumberFormat;
import java.util.Locale;

public class ConfirmTransactionActivity extends AppCompatActivity {

    public static final String TRANSACTION_SENT = "com.eletac.tronwallet.block_explorer_updater.transaction_sent";

    public static final String TRANSACTION_DATA_EXTRA = "transaction_data_extra";
    public static final String TRANSACTION_DATA2_EXTRA = "transaction_data2_extra";

    public static final int TRANSACTION_FINISHED = 4325;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ContractLoaderFragment mContract_Fragment;
    private TextView mCurrentBandwidth_TextView;
    private TextView mEstBandwidthCost_TextView;
    private TextView mNewBandwidth_TextView;
    private ConstraintLayout mNotEnoughBandwidth_ConstraintLayout;
    private TextView mTRX_Cost_TextView;
    private Button mGetBandwidth_Button;
    private TextInputLayout mPassword_Layout;
    private TextInputEditText mPassword_EditText;
    private Button mConfirm_Button;
    private CardView mBandwidth_CardView;

    private byte[] mTransactionBytes;
    private byte[] mExtraBytes;
    private double mTRX_Cost;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SignTransactionActivity.TRANSACTION_SIGN_REQUEST_CODE) {
            byte[] transactionData = data.getByteArrayExtra(SignTransactionActivity.TRANSACTION_SIGNED_EXTRA);

            updateConfirmButton();
            setupBandwidth();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transaction);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.ConfirmTrans_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContract_Fragment = (ContractLoaderFragment) getSupportFragmentManager().findFragmentById(R.id.ConfirmTrans_contract_fragment);
        mCurrentBandwidth_TextView = findViewById(R.id.ConfirmTrans_current_bandwidth_textView);
        mEstBandwidthCost_TextView = findViewById(R.id.ConfirmTrans_est_bandwidth_cost_textView);
        mNewBandwidth_TextView = findViewById(R.id.ConfirmTrans_new_bandwidth_textView);
        mNotEnoughBandwidth_ConstraintLayout = findViewById(R.id.ConfirmTrans_not_enough_bandwidth_constrain);
        mTRX_Cost_TextView = findViewById(R.id.ConfirmTrans_trx_cost_textView);
        mPassword_EditText = findViewById(R.id.ConfirmTrans_password_editText);
        mPassword_Layout = findViewById(R.id.ConfirmTrans_password_textInputLayout);
        mGetBandwidth_Button = findViewById(R.id.ConfirmTrans_get_bandwidth_button);
        mConfirm_Button = findViewById(R.id.ConfirmTrans_confirm_button);
        mBandwidth_CardView = findViewById(R.id.ConfirmTrans_bandwidth_cardView);

        Bundle extras = getIntent().getExtras();
        try {
            mTransactionBytes = extras.getByteArray(TRANSACTION_DATA_EXTRA);
            mExtraBytes = extras.getByteArray(TRANSACTION_DATA2_EXTRA);
        } catch ( DecoderException | NullPointerException ignored) {
            Toast.makeText(this, R.string.could_not_parse_transaction, Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        setupBandwidth();
        updateConfirmButton();


        mConfirm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPassword_EditText.getText().toString();

                if (isTransactionSigned()) {
                    if (mTRX_Cost > 0) {

                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        numberFormat.setMaximumFractionDigits(6);

                        new LovelyStandardDialog(ConfirmTransactionActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColor(Color.WHITE)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle(R.string.attention)
                                .setMessage(String.format("%s %s %s", getString(R.string.transaction_will_cost), numberFormat.format(mTRX_Cost), getString(R.string.trx_symbol)))
                                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        broadcastTransaction();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    } else {
                        broadcastTransaction();
                    }
                } else {
                    new LovelyInfoDialog(ConfirmTransactionActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(R.string.failed)
                            .setMessage(R.string.wrong_password)
                            .show();
                }
            }
        });

        mGetBandwidth_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmTransactionActivity.this, SendReceiveActivity.class);
                intent.putExtra("page", 2);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resetSign();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void broadcastTransaction() {
        LovelyProgressDialog progressDialog = new LovelyProgressDialog(ConfirmTransactionActivity.this)
                .setIcon(R.drawable.ic_send_white_24px)
                .setTitle(R.string.sending)
                .setTopColorRes(R.color.colorPrimary);
        progressDialog.show();

        mConfirm_Button.setEnabled(false);
        AsyncJob.doInBackground(() -> {
        });
    }

    private void resetSign() {
        mPassword_EditText.setText("");
        updateConfirmButton();
        setupBandwidth();
    }

    private void setupBandwidth() {
    }

    private void updateConfirmButton() {
    }

    private boolean isTransactionSigned() {
        return true;
    }
}
