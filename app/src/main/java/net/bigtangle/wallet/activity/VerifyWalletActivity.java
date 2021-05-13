package net.bigtangle.wallet.activity;

import android.Manifest;
import android.content.Intent;
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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.update.UpdateManager;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class VerifyWalletActivity extends AppCompatActivity {

    @BindView(R.id.verify_password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.verify_password_button)
    Button verifyWalletButton;

    private UpdateManager mUpdateManager;
    private String SP_PRIVACY = "sp_privacy";
    private boolean isCheckPrivacy = false;
    private static final int NOT_NOTICE = 2; //如果勾选了不再询问

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requetPermission();

        if (this.checkVersion()) {
            return;
        }

        setContentView(R.layout.activity_verify_wallet);
        ButterKnife.bind(this);

        //isCheckPrivacy = (boolean) SPUtil.get(VerifyWalletActivity.this, SP_PRIVACY, false);

        //if (!isCheckPrivacy) {
          //  showPrivacy();
       // } else {
            Toast.makeText(VerifyWalletActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();

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
       // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            //由于不知道是否选择了允许所以需要再次判断
            requetPermission();
        }
    }

    private void requetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + VerifyWalletActivity.this.getString(R.string.permissions) + permissions[i] + VerifyWalletActivity.this.getString(R.string.successful_application), Toast.LENGTH_SHORT).show();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
                        new LovelyStandardDialog(VerifyWalletActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColor(Color.WHITE)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(VerifyWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage(VerifyWalletActivity.this.getString(R.string.click_permit))
                                .setPositiveButton(VerifyWalletActivity.this.getString(R.string.to_allow), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    } else {//选择禁止
                        new LovelyStandardDialog(VerifyWalletActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColor(Color.WHITE)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(VerifyWalletActivity.this.getString(R.string.dialog_title_info))
                                .setMessage(VerifyWalletActivity.this.getString(R.string.click_permit))
                                .setPositiveButton(VerifyWalletActivity.this.getString(R.string.to_allow), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(VerifyWalletActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    }
                }
            }
        }
    }

    private boolean checkVersion() {
        //这里来检测版本是否需要更新
        setContentView(R.layout.progress);
        mUpdateManager = new UpdateManager(this);
        return mUpdateManager.checkUpdateInfo();
    }

    /**
     * 显示隐私政策或跳转到其他界面
     */
    private void check() {

        //先判断是否显示了隐私政策

        isCheckPrivacy = (boolean) SPUtil.get(VerifyWalletActivity.this, SP_PRIVACY, false);

        if (!isCheckPrivacy) {
            showPrivacy();
        } else {
            Toast.makeText(VerifyWalletActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示用户协议和隐私政策
     */
    private void showPrivacy() {

        final PrivacyDialog dialog = new PrivacyDialog(VerifyWalletActivity.this);
        TextView tv_privacy_tips = dialog.findViewById(R.id.tv_privacy_tips);
        TextView btn_exit = dialog.findViewById(R.id.btn_exit);
        TextView btn_enter = dialog.findViewById(R.id.btn_enter);
        dialog.show();

        String string = getResources().getString(R.string.privacy_tips);
        String key1 = getResources().getString(R.string.privacy_tips_key1);
        String key2 = getResources().getString(R.string.privacy_tips_key2);
        int index1 = string.indexOf(key1);
        int index2 = string.indexOf(key2);

        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.selected_time_text));
        spannedString.setSpan(colorSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.selected_time_text));
        spannedString.setSpan(colorSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击字体大小
        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan sizeSpan2 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击事件
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyWalletActivity.this, TermsActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyWalletActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //设置点击后的颜色为透明，否则会一直出现高亮
        tv_privacy_tips.setHighlightColor(Color.TRANSPARENT);
        //开始响应点击事件
        tv_privacy_tips.setMovementMethod(LinkMovementMethod.getInstance());

        tv_privacy_tips.setText(spannedString);

        //设置弹框宽度占屏幕的80%
        WindowManager m = getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (defaultDisplay.getWidth() * 0.80);
        dialog.getWindow().setAttributes(params);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(VerifyWalletActivity.this, SP_PRIVACY, false);
                finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                SPUtil.put(VerifyWalletActivity.this, SP_PRIVACY, true);

                Toast.makeText(VerifyWalletActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
