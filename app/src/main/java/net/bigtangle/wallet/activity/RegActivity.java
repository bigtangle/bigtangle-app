package net.bigtangle.wallet.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.security.realidentity.ALRealIdentityCallback;
import com.alibaba.security.realidentity.ALRealIdentityCallbackExt;
import com.alibaba.security.realidentity.ALRealIdentityResult;
import com.alibaba.security.realidentity.RPEventListener;
import com.alibaba.security.realidentity.RPResult;
import com.alibaba.security.realidentity.RPVerify;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.http.URLUtil;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.alibaba.security.rp.RPSDK.getContext;

public class RegActivity extends AppCompatActivity {
    public static final String HTTPS_BIGTANGLE = WalletContextHolder.getMBigtangle();

    String signin;
    String password;
    private String SP_PRIVACY = "sp_privacy";
    private boolean isCheckPrivacy = false;
    private static final int NOT_NOTICE = 2; //如果勾选了不再询问

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        showPrivacy();
//        String un = (String) SPUtil.get(RegActivity.this, "username", "");
//        String pwd = (String) SPUtil.get(RegActivity.this, "password", "");
//        if (un != null && !"".equals(un.trim())) {
//            Intent intent = new Intent(RegActivity.this, VerifyWalletActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClicked();
            }
        });

    }

    private void startClicked() {

        EditText textSignin = (EditText) findViewById(R.id.textSignin);
        signin = textSignin.getText().toString();
        EditText textPassword = (EditText) findViewById(R.id.textPassword);
        password = textPassword.getText().toString();
        try {
            doReg();
            showlog("注册成功");

            SPUtil.put(RegActivity.this, "username", signin);
            SPUtil.put(RegActivity.this, "password", password);
            Intent intent = new Intent(RegActivity.this, VerifyWalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("404")) {
                msg = "该手机号码已被注册";
            }
            showlog(msg);
        }

    }



    private void doReg() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        @SuppressWarnings({"unchecked", "rawtypes"}) final Future<String> handler = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                OkHttpClient client = OkHttp3Util.getUnsafeOkHttpClient();

                Request request = new Request.Builder().url(HTTPS_BIGTANGLE +
                        "/public/reg?username=" + signin + "&password=" + password).get().build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                } else {
                    throw new RuntimeException("" + response);
                }
                return "";
            }
        });
    }

    private void showlog(String log) {
        new LovelyInfoDialog(RegActivity.this)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_error_white_24px)
                .setTitle(RegActivity.this.getString(R.string.dialog_title_info))
                .setMessage(log)
                .show();


    }

    private void showPrivacy() {

        final PrivacyDialog dialog = new PrivacyDialog(RegActivity.this);
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
                Intent intent = new Intent(RegActivity.this, TermsActivity.class);
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
                Intent intent = new Intent(RegActivity.this, PrivacyPolicyActivity.class);
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
                SPUtil.put(RegActivity.this, SP_PRIVACY, false);
               // finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                SPUtil.put(RegActivity.this, SP_PRIVACY, true);

                Toast.makeText(RegActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();

            }
        });

    }

}