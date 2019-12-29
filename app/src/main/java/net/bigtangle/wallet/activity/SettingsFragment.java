package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.utils.BasicAuthInterceptor;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.SettingConnectionActivity;
import net.bigtangle.wallet.activity.settings.SettingContactActivity;
import net.bigtangle.wallet.activity.settings.SettingVersionActivity;
import net.bigtangle.wallet.activity.settings.SettingWalletActivity;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.core.WalletContextHolder;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static net.bigtangle.utils.OkHttp3Util.contentHex;
import static net.bigtangle.utils.OkHttp3Util.pubkey;
import static net.bigtangle.utils.OkHttp3Util.signHex;

public class SettingsFragment extends BaseLazyFragment {

    @BindView(R.id.connection_button)
    Button connectionButton;

    @BindView(R.id.wallet_button)
    Button walletButton;

    @BindView(R.id.about_button)
    Button aboutButton;

    @BindView(R.id.contact_button)
    Button contactButton;

    @BindView(R.id.token_button)
    Button tokenButton;

    private static long timeoutMinute = 16L;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void initEvent() {
        this.connectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingConnectionActivity.class);
                startActivity(intent);
            }
        });

        this.walletButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingWalletActivity.class);
                startActivity(intent);
            }
        });

        this.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingContactActivity.class);
                startActivity(intent);
            }
        });

        if (aboutButton != null) {
            this.aboutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SettingVersionActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (this.tokenButton != null) {
            this.tokenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ECKey ecKey = WalletContextHolder.get().walletKeys().get(0);
                                OkHttpClient client = getUnsafeOkHttpClient();
                                Request request = new Request.Builder().url("https://m.bigtangle.net/accessToken/generate?pubKey=" + ecKey.getPublicKeyAsHex()).get().build();
                                Response response = client.newCall(request).execute();
                                String accessToken = response.body().string();

                                byte[] buf = Utils.HEX.decode(accessToken);
                                byte[] bytes = ECIESCoder.decrypt(ecKey.getPrivKey(), buf);
                                String verifyHex = new String(bytes);

                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse("https://m.bigtangle.net/wallet/balance.jsf?accessToken=" + verifyHex);
                                intent.setData(content_url);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            X509TrustManager tr = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[]) null, new TrustManager[]{tr}, (SecureRandom) null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient client = (new okhttp3.OkHttpClient.Builder()).sslSocketFactory(sslSocketFactory, tr).hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            }).connectTimeout(timeoutMinute, TimeUnit.MINUTES).writeTimeout(timeoutMinute, TimeUnit.MINUTES).addInterceptor(new BasicAuthInterceptor(pubkey, signHex, contentHex)).readTimeout(timeoutMinute, TimeUnit.MINUTES).build();
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
