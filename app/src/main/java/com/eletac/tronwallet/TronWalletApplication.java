package com.eletac.tronwallet;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.eletac.tronwallet.block_explorer.BlockExplorerUpdater;
import com.eletac.tronwallet.wallet.AccountUpdater;
import com.eletac.tronwallet.wallet.PriceUpdater;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class TronWalletApplication extends Application implements LifecycleObserver {

    public static final String DATABASE_FILE_NAME = "tron_wallet.db";

    public static final String FOREGROUND_CHANGED = "com.eletac.tronwallet.app.foreground_changed";

    public static final long BLOCKCHAIN_UPDATE_INTERVAL = Long.MAX_VALUE; // not used - using singleShots only
    public static final long NETWORK_UPDATE_INTERVAL = Long.MAX_VALUE; // not used - using singleShots only
    public static final long TOKENS_UPDATE_INTERVAL = Long.MAX_VALUE; // not used - using singleShots only
    public static final long ACCOUNTS_UPDATE_INTERVAL = Long.MAX_VALUE; // not used - using singleShots only

    public static final long ACCOUNT_UPDATE_FOREGROUND_INTERVAL = 2000;
    public static final long ACCOUNT_UPDATE_BACKGROUND_INTERVAL = 15000;
    public static final long PRICE_UPDATE_INTERVAL = 15000;

    private FirebaseAnalytics mFirebaseAnalytics;
    private static Context mContext;
    private static boolean mIsInForeground;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());

        mContext = getApplicationContext();

        mIsInForeground = false;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        Map<BlockExplorerUpdater.UpdateTask, Long> updaterIntervals = new HashMap<>();
        updaterIntervals.put(BlockExplorerUpdater.UpdateTask.Blockchain, BLOCKCHAIN_UPDATE_INTERVAL);
        updaterIntervals.put(BlockExplorerUpdater.UpdateTask.Nodes, NETWORK_UPDATE_INTERVAL);
        updaterIntervals.put(BlockExplorerUpdater.UpdateTask.Witnesses, NETWORK_UPDATE_INTERVAL);
        updaterIntervals.put(BlockExplorerUpdater.UpdateTask.Tokens, TOKENS_UPDATE_INTERVAL);
        updaterIntervals.put(BlockExplorerUpdater.UpdateTask.Accounts, ACCOUNTS_UPDATE_INTERVAL);
        BlockExplorerUpdater.init(this, updaterIntervals);

        AccountUpdater.init(this, ACCOUNT_UPDATE_FOREGROUND_INTERVAL);
        PriceUpdater.init(this, PRICE_UPDATE_INTERVAL);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("logged_previous_transactions", false)) {
        }
    }

    public static Context getAppContext() {
        return TronWalletApplication.mContext;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        mIsInForeground = false;
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(FOREGROUND_CHANGED));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        mIsInForeground = true;
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(FOREGROUND_CHANGED));
    }

    public static boolean isIsInForeground() {
        return mIsInForeground;
    }
}
