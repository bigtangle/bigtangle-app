package net.bigtangle.wallet.components;

import android.app.Application;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.utils.WalletFileUtils;

public class BigtangleWlletApplication extends Application implements LifecycleObserver {

    public void onCreate() {
        super.onCreate();
        this.initLocalStorage();
        this.initWallet();
    }

    private void initWallet() {
        try {
            WalletFileUtils.createWalletDB(getApplicationContext());
        }catch (Exception e){

        }



    }

    private void initLocalStorage() {
        LocalStorageContext localStorageContext = LocalStorageContext.ini(getApplicationContext());
        localStorageContext.initData();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
    }
}
