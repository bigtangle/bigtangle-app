package net.bigtangle.wallet.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.wallet.WalletAccountFragment;
import net.bigtangle.wallet.core.http.URLUtil;
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


    public static String check(Context context) throws Exception {

        InputStream stream = CommonUtil.loadFromDB("", context);
        WalletContextHolder.loadWallet(stream);

        ECKey ecKey = WalletContextHolder.walletKeys().get(0);
        String url = WalletContextHolder.getMBigtangle() +
                "/accessToken/generate?pubKey=" + ecKey.getPublicKeyAsHex();


        String accessToken = new URLUtil().calculateString(url).get();

        return accessToken;
    }

    public static void open(Context context, String url, String accessToken) throws Exception {
        // InputStream stream = CommonUtil.loadFromDB("", context);
        //WalletContextHolder.loadWallet(stream);
        ECKey ecKey = WalletContextHolder.walletKeys().get(0);

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
