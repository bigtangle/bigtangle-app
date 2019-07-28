package net.bigtangle.wallet.activity.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import net.bigtangle.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletSecretkeyDialog extends Dialog {

    private Context context;
    protected View mContextView = null;

    @BindView(R.id.public_key_text_input)
    TextInputEditText publicKeyInput;

    @BindView(R.id.private_key_text_input)
    TextInputEditText privateKeyInput;

    @BindView(R.id.negative_button)
    Button negativeButton;

    @BindView(R.id.positive_button)
    Button positiveButton;

    public WalletSecretkeyDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    public void show() {
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        super.show();
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

    public WalletSecretkeyDialog setListenter(final OnGetWalletSecretKeyListenter listener) {
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
        return this;
    }

    public interface OnGetWalletSecretKeyListenter {
        void getWalletSecretKey(String publicKey, String privateKey);
    }
}
