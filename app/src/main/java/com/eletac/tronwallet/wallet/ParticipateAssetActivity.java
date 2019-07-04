package com.eletac.tronwallet.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eletac.tronwallet.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ParticipateAssetActivity extends AppCompatActivity {

    public static final String ASSET_NAME_EXTRA = "asset_name_extra";

    private TextView mName_TextView;
    private TextView mDescription_TextView;
    private TextView mSupply_TextView;
    private TextView mIssuer_TextView;
    private TextView mStart_TextView;
    private TextView mEnd_TextView;
    private TextView mPrice_TextView;

    private EditText mAmount_EditText;
    private SeekBar mAmount_SeekBar;
    private TextView mCost_TextView;
    private Button mSpend_Button;

    private double mTokenPrice;

    private boolean mUpdatingAmount = false;
    private long mAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participate_asset);

        mName_TextView = findViewById(R.id.ParticipateAsset_name_textView);
        mDescription_TextView = findViewById(R.id.ParticipateAsset_description_textView);
        mSupply_TextView = findViewById(R.id.ParticipateAsset_supply_textView);
        mIssuer_TextView = findViewById(R.id.ParticipateAsset_issuer_textView);
        mStart_TextView = findViewById(R.id.ParticipateAsset_start_textView);
        mEnd_TextView = findViewById(R.id.ParticipateAsset_end_textView);
        mPrice_TextView = findViewById(R.id.ParticipateAsset_price_textView);

        mAmount_EditText = findViewById(R.id.ParticipateAsset_amount_editText);
        mAmount_SeekBar = findViewById(R.id.ParticipateAsset_amount_seekBar);
        mCost_TextView = findViewById(R.id.ParticipateAsset_cost_textView);
        mSpend_Button = findViewById(R.id.ParticipateAsset_spend_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String assetName = extras.getString(ASSET_NAME_EXTRA);
            if (assetName != null && !assetName.isEmpty()) {
            } else {
                finish();
                return;
            }
        }
    }

    private void updateCost() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(6);
        double cost = (mAmount * mTokenPrice / 1000000D);
        mCost_TextView.setText(numberFormat.format(cost));
    }
}
