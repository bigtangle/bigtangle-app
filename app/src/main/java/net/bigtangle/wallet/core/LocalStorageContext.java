package net.bigtangle.wallet.core;

import android.content.Context;
import android.content.SharedPreferences;

import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.utils.RoutePathUtil;

public class LocalStorageContext {

    final String walletName = "bigtangle-wallet";

    private Context context;

    public LocalStorageContext(Context context) {
        this.context = context;
    }

    private static LocalStorageContext instance;

    public static final LocalStorageContext get() {
        return instance;
    }

    public static final LocalStorageContext ini(Context context) {
        instance = new LocalStorageContext(context);
        return instance;
    }

    public void initData() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean b = sharedPreferences.getBoolean("init", false);
        if (!b) {
            editor.putBoolean("init", true);
            editor.putString("serverURL", "https://bigtangle.info/");
            editor.commit();
        }
        HttpConnectConstant.HTTP_SERVER_URL = sharedPreferences.getString("serverURL", "https://bigtangle.info/");
    }

    public void writeServerURL(String serverURL) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("serverURL", serverURL);
        editor.commit();
        HttpConnectConstant.HTTP_SERVER_URL = sharedPreferences.getString("serverURL", "https://bigtangle.info/");
    }

    public String readServerURL() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("serverURL", "https://bigtangle.info/");
    }

    public void writeWalletPath(String directory, String prefix) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wallet.directory", directory);
        editor.putString("wallet.prefix", prefix);
        editor.commit();
    }

    public String readWalletDirectory() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("wallet.directory", RoutePathUtil.getBasePath(this.context));
    }

    public String readWalletFilePrefix() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("wallet.prefix", "bigtangle");
    }
}
