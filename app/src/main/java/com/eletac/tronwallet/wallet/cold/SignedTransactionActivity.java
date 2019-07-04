package com.eletac.tronwallet.wallet.cold;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.eletac.tronwallet.R;

public class SignedTransactionActivity extends AppCompatActivity {

    public static final String TRANSACTION_DATA_EXTRA = "transaction_data_extra";

    private ImageView mSignedTransactionQR_ImageView;
    private ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_transaction);

        mSignedTransactionQR_ImageView = findViewById(R.id.SignedTransaction_qr_imageView);
        mConstraintLayout = findViewById(R.id.SignedTransaction_constraintLayout);

        mConstraintLayout.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        }
    }


    private void showQR() {
        mConstraintLayout.setVisibility(View.VISIBLE);
        mConstraintLayout.setAlpha(0);
        mConstraintLayout.setScaleX(0);
        mConstraintLayout.setScaleY(0);
        mConstraintLayout.animate().setDuration(350).alpha(1).scaleX(1).scaleY(1).setStartDelay(200).start();
    }
}