package net.bigtangle.wallet.components;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.utils.CommonUtil;
import net.bigtangle.wallet.core.utils.WalletFileUtils;

import java.io.InputStream;

public class BigtangleWlletApplication extends Application implements LifecycleObserver {

    public void onCreate() {
        super.onCreate();
        this.initLocalStorage();
        this.initWallet();
    }

    private void initWallet() {
        String un = SPUtil.get(this, "username", "").toString();
        InputStream stream = CommonUtil.loadFromDB(un, this);
        WalletContextHolder.loadWallet(stream);
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
        WalletContextHolder walletContextHolder = WalletContextHolder.get();
        if (!walletContextHolder.checkWalletExists()) {
            WalletFileUtils.createWalletFileAndLoad();
        }
        walletContextHolder.initData();

//        if (walletContextHolder.walletKeys() == null ){
//            WalletFileUtils.createWalletFileAndLoad();
//        }
//        if (walletContextHolder.walletKeys() != null && walletContextHolder.walletKeys().size()<=0){
//            WalletFileUtils.createWalletFileAndLoad();
//        }
//        walletContextHolder.initData();
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
