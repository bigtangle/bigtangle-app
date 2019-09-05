package net.bigtangle.wallet.activity.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;

import org.apache.commons.lang3.StringUtils;

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

    private OnGetWalletSecretKeyListenter listener;

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
        if (positiveButton != null) {
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pubKeyStr = publicKeyInput.getText().toString();
                    if (StringUtils.isBlank(pubKeyStr)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage(context.getString(R.string.public_key_address_not_empty))
                                .show();
                        return;
                    }
                    String privKeyStr = privateKeyInput.getText().toString();
                    if (StringUtils.isBlank(privKeyStr)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage(context.getString(R.string.private_key_address_not_empty))
                                .show();
                        return;
                    }
                    if (listener != null) {
                        listener.getWalletSecretKey(pubKeyStr, privKeyStr);
                    }
                    dismiss();
                }
            });
        }
    }

    public WalletSecretkeyDialog setListenter(final OnGetWalletSecretKeyListenter listener) {
        this.listener = listener;
        return this;
    }

    public interface OnGetWalletSecretKeyListenter {
        void getWalletSecretKey(String publicKey, String privateKey);
    }
}
