package net.bigtangle.wallet.components;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.utils.RoutePathUtil;

public class ApplicationContext extends Application implements LifecycleObserver {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        WalletContextHolder.get().initWalletData(RoutePathUtil.getBasePath(this), "bigtangle");
        this.initLocalStorage();
    }

    private void initLocalStorage() {
        LocalStorageContext localStorageContext = LocalStorageContext.ini(this.mContext);
        localStorageContext.initData();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
    }
}
