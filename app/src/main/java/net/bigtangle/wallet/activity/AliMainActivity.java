package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.security.realidentity.ALRealIdentityCallback;
import com.alibaba.security.realidentity.ALRealIdentityCallbackExt;
import com.alibaba.security.realidentity.ALRealIdentityResult;
import com.alibaba.security.realidentity.RPEventListener;
import com.alibaba.security.realidentity.RPResult;
import com.alibaba.security.realidentity.RPVerify;

import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;

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

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

public class AliMainActivity extends AppCompatActivity {
    public static final String HTTPS_BIGTANGLE = WalletContextHolder.getMBigtangle();
    String verifyToken;
    String bizId;
    String signin;
    String password;
    String walletpwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

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
        EditText textWalletPwd = (EditText) findViewById(R.id.textWalletPwd);
        walletpwd = textWalletPwd.getText().toString();
        try {
            verfiyToken();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("417")) {
                msg = " 钱包大网币数量不足 ";
            }
            if (msg.contains("404")) {
                msg = " 用户名或密码不对，请重新输入 ";
            }
            if (msg.contains("412")) {
                msg = " 钱包密码错误 ";
            }
            if (msg.contains("413")) {
                msg = " 重复认证 ";
            }
            showlog(" 错误: " + msg);
            //  ((EditText) findViewById(R.id.textMSG)).setText(msg);
            return;
        }

        RPVerify.start(AliMainActivity.this,
                verifyToken,
                new RPEventListener() {
                    @Override
                    public void onFinish(RPResult auditResult, String code, String msg) {
                        if (auditResult == RPResult.AUDIT_PASS) {
                            // 认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                            // do something
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Uri content_url = Uri.parse(HTTPS_BIGTANGLE +
                                            "/wallet/identity4android.jsf" + "?bizId=" + bizId
                                            + "&user_access_token=" + bizId + "&walletpwd=" + walletpwd);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, content_url);
                                    startActivity(browserIntent);
                                }
                            }).start();
                        } else {
                            showlog(msg);
                        }
                    }
                });


    }

    private void showlog(String log) {
        new LovelyInfoDialog(AliMainActivity.this)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_error_white_24px)
                .setTitle(AliMainActivity.this.getString(R.string.dialog_title_info))
                .setMessage(log)
                .show();


    }

    /**
     * 基础回调的方式 TODO
     *
     * @return
     */
    private ALRealIdentityCallback getALRealIdentityCallback() {
        return new ALRealIdentityCallback() {
            @Override
            public void onAuditResult(ALRealIdentityResult alRealIdentityResult, String s) {
                //DO your things
                Log.d("RPSDK", "ALRealIdentityResult:" + alRealIdentityResult.audit);
            }
        };
    }

    /**
     * 增加活体页面开始与结束回调 TODO
     *
     * @return
     */
    private ALRealIdentityCallbackExt getALRealIdentityCallbackEX() {
        return new ALRealIdentityCallbackExt() {
            @Override
            public void onBiometricsStart() {
                //活体页面开始
            }

            @Override
            public void onBiometricsStop(boolean isBiometricsSuc) {//
                //活体页面结束 参数表示是否检测成功
            }

            @Override
            public void onAuditResult(ALRealIdentityResult alRealIdentityResult, String s) {
                //DO your things
                Log.d("RPSDK", "ALRealIdentityResult:" + alRealIdentityResult.audit);
            }
        };
    }


    private void verfiyToken()
            throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        @SuppressWarnings({"unchecked", "rawtypes"}) final Future<String> handler = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                getVerifyToken();
                return "";
            }
        });
        try {
            handler.get(600000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
        } finally {
            executor.shutdownNow();
        }


    }

    public void getVerifyToken() throws Exception {
        bizId = UUID.randomUUID().toString();
        OkHttpClient client = OkHttp3Util.getUnsafeOkHttpClient();

        Request request = new Request.Builder().url(HTTPS_BIGTANGLE +
                "/public/aliyunHuaweiToken?bizId=" + bizId
                + "&signin=" + signin + "&password=" + password + "&walletpwd=" + walletpwd).get().build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            verifyToken = response.body().string();

        } else {
            //Display again
            throw new RuntimeException("" + response);

        }
    }
}
