package net.bigtangle.wallet.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletInputPasswordDialog extends Dialog {

    private Context context;

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.positive_button)
    Button positiveButton;

    private OnGetWalletPasswordListenter listenter;

    public WalletInputPasswordDialog(Context context, int theme, OnGetWalletPasswordListenter listenter) {
        super(context, theme);
        this.context = context;
        this.listenter = listenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_wallet_password, null);

        setContentView(view);
        ButterKnife.bind(this, view);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = passwordTextInput.getText().toString();
                boolean b = WalletContextHolder.get().saveAndCheckPassword(password);
                if (!b) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(context.getString(R.string.dialog_title_info))
                            .setMessage("输入密码不正确")
                            .show();
                    return;
                }

                if (listenter != null) {
                    listenter.getWalletPassword(password);
                }
                dismiss();
            }
        });
    }

    public interface OnGetWalletPasswordListenter {
        void getWalletPassword(String password);
    }
}
