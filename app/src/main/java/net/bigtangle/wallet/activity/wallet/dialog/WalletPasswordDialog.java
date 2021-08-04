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
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletPasswordDialog extends Dialog {

    private Context context;

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.positive_button)
    Button positiveButton;

    private OnWalletVerifyPasswordListenter listenter;

    public WalletPasswordDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public WalletPasswordDialog setListenter(OnWalletVerifyPasswordListenter listenter) {
        this.listenter = listenter;
        return this;
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
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_wallet_password, null);

        setContentView(view);
        ButterKnife.bind(this, view);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = passwordTextInput.getText().toString();
                String un = SPUtil.get(context, "username", "").toString();
                InputStream stream = CommonUtil.loadFromDB(un, context);
                WalletContextHolder.loadWallet(stream);

                boolean b = WalletContextHolder.saveAndCheckPassword(password);
                if (!b) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(context.getString(R.string.dialog_title_info))
                            .setMessage(context.getString(R.string.input_password_incorrect))
                            .show();
                    return;
                }
                if (listenter != null) {
                    listenter.verifyPassword(password);
                }
                dismiss();
            }
        });
    }

    public interface OnWalletVerifyPasswordListenter {

        void verifyPassword(String password);
    }
}
