package net.bigtangle.wallet.activity.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.utils.CommonUtil;
import net.bigtangle.wallet.core.utils.WalletFileUtils;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletDownfileDialog extends Dialog {

    private Context context;
    protected View mContextView = null;

    @BindView(R.id.username_text_input)
    TextInputEditText usernameTextInput;

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.negative_button)
    Button negativeButton;

    @BindView(R.id.positive_button)
    Button positiveButton;

    private OnWalletDownfileListenter listener;

    public WalletDownfileDialog(Context context, int theme) {
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
        mContextView = LayoutInflater.from(context).inflate(R.layout.dialog_wallet_downfile, null);
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
                    String username = usernameTextInput.getText().toString();
                    if (StringUtils.isBlank(username)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage(R.string.username_not_empty)
                                .show();
                        return;
                    }
                    String password = passwordTextInput.getText().toString();
                    if (StringUtils.isBlank(password)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage(R.string.password_not_empty)
                                .show();
                        return;
                    }
                    CommonUtil.backupFile("bigtangle", context);
                    WalletFileUtils.download(username, password, listener,context);
                    dismiss();
                }
            });
        }
    }

    public WalletDownfileDialog setListenter(WalletDownfileDialog.OnWalletDownfileListenter listenter) {
        this.listener = listenter;
        return this;
    }

    public interface OnWalletDownfileListenter {

        void downloadFileStatus(boolean success,Exception e);
    }
}
