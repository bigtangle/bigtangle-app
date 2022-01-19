package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.update.UpdateManager;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyWalletActivity extends AppCompatActivity {

    @BindView(R.id.verify_password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.verify_password_button)
    Button verifyWalletButton;
    private UpdateManager mUpdateManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (this.checkVersion()) {
            return;
        }
        setContentView(R.layout.activity_verify_wallet);

        ButterKnife.bind(this);


        checkPassword();

    }

    public void checkPassword() {


        if (WalletContextHolder.checkWalletHavePassword()) {

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

                    boolean b = WalletContextHolder.saveAndCheckPassword(password);
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
//                    finish();
                }
            });
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            finish();
        }

    }

    private boolean checkVersion() {
        //这里来检测版本是否需要更新
        setContentView(R.layout.progress);
        mUpdateManager = new UpdateManager(this);
        return mUpdateManager.checkUpdateInfo();
    }


}
