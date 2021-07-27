package net.bigtangle.wallet.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.kits.WalletUtil;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.MySQLiteOpenHelper;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.update.UpdateManager;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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
