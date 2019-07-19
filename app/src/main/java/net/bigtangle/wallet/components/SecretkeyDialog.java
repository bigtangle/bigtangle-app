package net.bigtangle.wallet.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.bigtangle.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecretkeyDialog extends Dialog {

    private Context context;
    protected View mContextView = null;

    @BindView(R.id.dialog_title)
    TextView dialogTitle;
    @BindView(R.id.public_key_input)
    TextInputEditText publicKeyInput;
    @BindView(R.id.private_key_input)
    TextInputEditText privateKeyInput;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.negative_button)
    Button negativeButton;
    @BindView(R.id.positive_button)
    Button positiveButton;

    public SecretkeyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public SecretkeyDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContextView = LayoutInflater.from(context).inflate(R.layout.dialog_wallet_secretkey, null);
        setContentView(mContextView);
        ButterKnife.bind(this, mContextView);
        initView();
    }

    private void initView() {

        if (negativeButton != null) {
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    public void setPositiveButton(final OnGetWalletSecretKeyListenter listener) {
        if (positiveButton != null) {
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.getWalletSecretKey(publicKeyInput.getText().toString(), privateKeyInput.getText().toString());
                    }
                    dismiss();
                }
            });
        }
    }

    public interface OnGetWalletSecretKeyListenter {
        void getWalletSecretKey(String publicKey, String privateKey);
    }


}
