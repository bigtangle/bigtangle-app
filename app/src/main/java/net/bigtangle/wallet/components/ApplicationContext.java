package net.bigtangle.wallet.components;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;

public class ApplicationContext extends Application implements LifecycleObserver {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        this.initLocalStorage();
        WalletContextHolder.get().initWalletData(LocalStorageContext.get().readWalletDirectory(),
                LocalStorageContext.get().readWalletFilePrefix());
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
