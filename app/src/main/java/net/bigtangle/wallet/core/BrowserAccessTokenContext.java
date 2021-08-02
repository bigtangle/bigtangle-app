package net.bigtangle.wallet.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.wallet.WalletAccountFragment;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BrowserAccessTokenContext {

    // private static BrowserAccessTokenContext instance = new BrowserAccessTokenContext();

    // public static final BrowserAccessTokenContext get() {
    //     return instance;
    // }


    public static void open(Context context, String url) throws Exception {
        String un = SPUtil.get(context, "username", "").toString();
        InputStream stream = CommonUtil.loadFromDB(un, context);
        WalletContextHolder.loadWallet(stream);

        ECKey ecKey = WalletContextHolder.walletKeys().get(0);
        OkHttpClient client = OkHttp3Util.getUnsafeOkHttpClient();

        Request request = new Request.Builder().url(WalletContextHolder.getMBigtangle() +
                "/accessToken/generate?pubKey=" + ecKey.getPublicKeyAsHex()).get().build();
        Response response = client.newCall(request).execute();
        String accessToken = response.body().string();

        byte[] buf = Utils.HEX.decode(accessToken);
        byte[] bytes = ECIESCoder.decrypt(ecKey.getPrivKey(), buf);
        String verifyHex = new String(bytes);

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url + "?user_access_token=" + verifyHex);
        intent.setData(content_url);
        context.startActivity(intent);
    }

}
