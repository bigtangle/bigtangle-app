package net.bigtangle.wallet.components;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import net.bigtangle.wallet.core.LocalStorageContext;

public class BigtangleWlletApplication extends Application implements LifecycleObserver {

    public void onCreate() {
        super.onCreate();
        this.initLocalStorage();
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
