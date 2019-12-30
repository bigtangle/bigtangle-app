package net.bigtangle.wallet.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.utils.OkHttp3Util;

import org.apache.commons.lang3.StringUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BrowserAccessTokenContext {

    private static BrowserAccessTokenContext instance = new BrowserAccessTokenContext();

    public static final BrowserAccessTokenContext get() {
        return instance;
    }

    private String accessToken;

    public void open(Context context, String url) throws Exception {
        if (StringUtils.isBlank(accessToken)) {
            ECKey ecKey = WalletContextHolder.get().walletKeys().get(0);
            OkHttpClient client = OkHttp3Util.getUnsafeOkHttpClient();

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
        Uri content_url = Uri.parse(url + "?user_access_token=" + accessToken);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    private static long timeoutMinute = 16L;
}
