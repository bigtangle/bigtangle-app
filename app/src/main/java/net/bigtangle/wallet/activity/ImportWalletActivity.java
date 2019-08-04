package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.utils.WalletFileUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImportWalletActivity extends AppCompatActivity {

    private static final int REQUESTCODE_FROM_ACTIVITY = 1000;

    @BindView(R.id.import_wallet_button)
    Button importWalletButton;

    @BindView(R.id.wallet_name_text_view)
    TextView walletNameTextView;

    @BindView(R.id.wallet_path_text_view)
    TextView walletPathTextView;

    @BindView(R.id.choose_wallet_button)
    Button chooseWalletButton;

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.create_wallet_button)
    Button createWalletButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        ButterKnife.bind(this);

        this.initView();
    }

    private void initView() {
        this.chooseWalletButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new LFilePicker()
                        .withActivity(ImportWalletActivity.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withStartPath(LocalStorageContext.get().readWalletDirectory())
                        .withIsGreater(false)
                        .withFileSize(500 * 1024)
                        .start();
            }
        });

        this.importWalletButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String directory = walletPathTextView.getText().toString();
                String prefix = walletNameTextView.getText().toString();

                if (StringUtils.isBlank(directory) || StringUtils.isBlank(prefix)) {
                    new LovelyInfoDialog(ImportWalletActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_info))
                            .setMessage("请选择钱包文件")
                            .show();
                    return;
                }

                WalletContextHolder.get().reloadWalletFile(directory, prefix);

                if (WalletContextHolder.get().checkWalletHavePassword()) {
                    final String password = passwordTextInput.getText().toString();

                    if (StringUtils.isBlank(password)) {
                        new LovelyInfoDialog(ImportWalletActivity.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage("密码不可以为空")
                                .show();
                        return;
                    }

                    boolean b = WalletContextHolder.get().saveAndCheckPassword(password);
                    if (!b) {
                        new LovelyInfoDialog(ImportWalletActivity.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage("输入密码不正确")
                                .show();
                        return;
                    }
                }

                LocalStorageContext.get().writeWalletPath(directory, prefix);

                WalletContextHolder.get().initData();

                Intent intent = new Intent(ImportWalletActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        this.createWalletButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WalletContextHolder walletContextHolder = WalletContextHolder.get();
                if (!walletContextHolder.checkWalletExists()) {
                    try {
                        WalletFileUtils.createWalletFileAndLoad();
                    } catch (IOException e) {
                        Log.e(LogConstant.TAG, "钱包创建失败", e);
                        return;
                    }
                }

                WalletContextHolder.get().initData();

                Intent intent = new Intent(ImportWalletActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.isEmpty()) {
                    new LovelyInfoDialog(ImportWalletActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_error))
                            .setMessage("当前选择文件错误")
                            .show();
                    return;
                }
                try {
                    File file = new File(list.get(0));
                    String directory = file.getParent() + "/";
                    String filename = file.getName();
                    String prefix = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
                    walletPathTextView.setText(directory);
                    walletNameTextView.setText(prefix);
                } catch (Exception e) {
                    Log.e(LogConstant.TAG, "wallet file", e);
                    new LovelyInfoDialog(ImportWalletActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_error))
                            .setMessage("当前选择文件错误")
                            .show();
                }
            }
        }
    }
}
