package net.bigtangle.wallet.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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
        myRequetPermission();
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
                            .setMessage(ImportWalletActivity.this.getString(R.string.please_select_your_wallet_file))
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
                                .setMessage(ImportWalletActivity.this.getString(R.string.password_not_empty))
                                .show();
                        return;
                    }

                    boolean b = WalletContextHolder.get().saveAndCheckPassword(password);
                    if (!b) {
                        new LovelyInfoDialog(ImportWalletActivity.this)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage(ImportWalletActivity.this.getString(R.string.input_password_incorrect))
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
                        Log.e(LogConstant.TAG, ImportWalletActivity.this.getString(R.string.wallet_creation_failed), e);
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
        if(requestCode==NOT_NOTICE){
            //由于不知道是否选择了允许所以需要再次判断
            myRequetPermission();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.isEmpty()) {
                    new LovelyInfoDialog(ImportWalletActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(ImportWalletActivity.this.getString(R.string.dialog_title_error))
                            .setMessage(ImportWalletActivity.this.getString(R.string.current_selection_file_error))
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
                            .setMessage(ImportWalletActivity.this.getString(R.string.current_selection_file_error))
                            .show();
                }
            }
        }
    }
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
    private AlertDialog alertDialog;
    private AlertDialog mDialog;

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            Toast.makeText(this,"您已经申请了权限!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])){//用户选择了禁止不再询问

                        AlertDialog.Builder builder = new AlertDialog.Builder(ImportWalletActivity.this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();



                    }else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(ImportWalletActivity.this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(ImportWalletActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                }
            }
        }
    }
}
