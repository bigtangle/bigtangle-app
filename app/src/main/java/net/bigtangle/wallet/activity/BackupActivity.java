package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.dialog.WalletPasswordDialog;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BackupActivity extends AppCompatActivity {
    private static final int REQUESTCODE_FROM_ACTIVITY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filename);


        findViewById(R.id.skip_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BackupActivity.this, VerifyWalletActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
        );

        EditText textInviter = (EditText) findViewById(R.id.filenameText);
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String now=dateFormat.format(new Date());
        textInviter.setText("backup-"+now);
        findViewById(R.id.btn_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText filenameText = (EditText) findViewById(R.id.filenameText);
                String filename = filenameText.getText().toString();
                if (filename == null || "".equals(filename.trim())) return;
                InputStream is = CommonUtil.loadFromDB("bigtangle", BackupActivity.this);
                if (is == null) return;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
               // String filepath = + ;
                   //     "/storage/emulated/0/Download/" + filename + ".wallet";
                String dirs="/storage/emulated/0/Download/";
                File dir=new File(dirs);
                if (!dir.exists())
                    dir.mkdirs();
                File file = new File(dir,filename + ".wallet" );
                try {
                    long total = is.available();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        //listener.onDownloading(progress);
                    }
                    fos.flush();
                    new LovelyInfoDialog(BackupActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle("")
                            .setMessage( getString(R.string.save)
                                            +
                                    file.getAbsoluteFile())
                            .show();
                } catch (Exception e) {
                    Log.e(LogConstant.TAG, "backup file", e);
                    new LovelyInfoDialog(BackupActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(getString(R.string.dialog_title_error))
                            .setMessage(getString(R.string.current_selection_file_error)
                            + "\n " + e.getLocalizedMessage())
                            .show();
                } finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }

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
                    new LovelyInfoDialog(BackupActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(getString(R.string.dialog_title_error))
                            .setMessage(getString(R.string.current_selection_file))
                            .show();
                    return;
                }
                try {
                    File file = new File(list.get(0));
                    InputStream uodateStram = new FileInputStream(file);
                    byte[] updateBytes = CommonUtil.urlTobyte(uodateStram);

                    String un = SPUtil.get(BackupActivity.this, "username", "").toString();
                    CommonUtil.updateDB(un, updateBytes, BackupActivity.this);
                    InputStream stream = CommonUtil.loadFromDB(un, BackupActivity.this);
                    WalletContextHolder.loadWallet(stream);

                    if (WalletContextHolder.checkWalletHavePassword()) {
                        new WalletPasswordDialog(BackupActivity.this, R.style.CustomDialogStyle)
                                .setListenter(new WalletPasswordDialog.OnWalletVerifyPasswordListenter() {

                                    @Override
                                    public void verifyPassword(String password) {

                                    }
                                }).show();
                    } else {

                    }
                } catch (Exception e) {
                    Log.e(LogConstant.TAG, "wallet file", e);
                    new LovelyInfoDialog(BackupActivity.this)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(getString(R.string.dialog_title_error))
                            .setMessage(getString(R.string.current_selection_file_error))
                            .show();
                }
            }
        }
    }


}
