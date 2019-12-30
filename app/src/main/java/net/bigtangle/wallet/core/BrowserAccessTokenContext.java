package net.bigtangle.wallet.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.utils.BasicAuthInterceptor;

import org.apache.commons.lang3.StringUtils;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static net.bigtangle.utils.OkHttp3Util.contentHex;
import static net.bigtangle.utils.OkHttp3Util.pubkey;
import static net.bigtangle.utils.OkHttp3Util.signHex;

public class BrowserAccessTokenContext {

    private static BrowserAccessTokenContext instance = new BrowserAccessTokenContext();

    public static final BrowserAccessTokenContext get() {
        return instance;
    }

    private String accessToken;

    public void open(Context context, String url) throws Exception {
        if (StringUtils.isBlank(accessToken)) {
            ECKey ecKey = WalletContextHolder.get().walletKeys().get(0);
            OkHttpClient client = getUnsafeOkHttpClient();
            Request request = new Request.Builder().url("https://m.bigtangle.net/accessToken/generate?pubKey=" + ecKey.getPublicKeyAsHex()).get().build();
            Response response = client.newCall(request).execute();
            String accessToken = response.body().string();

            byte[] buf = Utils.HEX.decode(accessToken);
            byte[] bytes = ECIESCoder.decrypt(ecKey.getPrivKey(), buf);
            String verifyHex = new String(bytes);
            this.accessToken = verifyHex;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url + "?accessToken=" + accessToken);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    private OkHttpClient getUnsafeOkHttpClient() {
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

    private static long timeoutMinute = 16L;
}
