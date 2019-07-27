package net.bigtangle.wallet.core.constant;

import android.content.Context;
import android.content.SharedPreferences;

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
}
