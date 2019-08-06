package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyWalletActivity extends AppCompatActivity {

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.verify_wallet_button)
    Button verifyWalletButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_wallet);
        ButterKnife.bind(this);

        /*if (!WalletContextHolder.get().checkWalletExists()) {
            Intent intent = new Intent(VerifyWalletActivity.this, ImportWalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        WalletContextHolder.get().initData();*/

        if (WalletContextHolder.get().checkWalletHavePassword()) {
            this.verifyWalletButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final String password = passwordTextInput.getText().toString();
                    if (StringUtils.isBlank(password)) {
                        new LovelyInfoDialog(VerifyWalletActivity.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(VerifyWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage(VerifyWalletActivity.this.getString(R.string.password_not_empty))
                                .show();
                        return;
                    }

                    boolean b = WalletContextHolder.get().saveAndCheckPassword(password);
                    if (!b) {
                        new LovelyInfoDialog(VerifyWalletActivity.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(VerifyWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage(VerifyWalletActivity.this.getString(R.string.input_password_incorrect))
                                .show();
                        return;
                    }

                    Intent intent = new Intent(VerifyWalletActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
