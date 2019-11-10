package net.bigtangle.wallet.core;

import android.content.Context;
import android.content.SharedPreferences;

import net.bigtangle.wallet.core.constant.HttpConnectConstant;

public class LocalStorageContext {

    private static final String walletName = "bigtangle-wallet";

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
            editor.putString("serverURL", "https://p.bigtangle.org:8088/");
            editor.commit();
        }
        HttpConnectConstant.HTTP_SERVER_URL = sharedPreferences.getString("serverURL", "https://p.bigtangle.org:8088/");
    }

    public void writeServerURL(String serverURL) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("serverURL", serverURL);
        editor.commit();
        HttpConnectConstant.HTTP_SERVER_URL = sharedPreferences.getString("serverURL", "https://p.bigtangle.org:8088/");
    }

    public String readServerURL() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("serverURL", "https://p.bigtangle.org:8088/");
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
        return sharedPreferences.getString("wallet.directory", "/storage/emulated/0/Download/");
    }

    public String readWalletFilePrefix() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("wallet.prefix", "bigtangle");
    }

    public boolean readInitFlag() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(walletName, Context.MODE_PRIVATE);
        boolean initFlag = sharedPreferences.getBoolean("init", false);
        return initFlag;
    }
}
