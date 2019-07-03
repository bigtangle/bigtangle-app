package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.arasthel.asyncjob.AsyncJob;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockExplorerUpdater {

    public enum UpdateTask {
        Blockchain,
        Nodes,
        Witnesses,
        Tokens,
        Accounts
    }

    public static final String BLOCKCHAIN_UPDATED = "com.eletac.tronwallet.block_explorer_updater.blockchain_updated";
    public static final String WITNESSES_UPDATED = "com.eletac.tronwallet.block_explorer_updater.witnesses_updated";
    public static final String NODES_UPDATED = "com.eletac.tronwallet.block_explorer_updater.nodes_updated";
    public static final String ACCOUNTS_UPDATED = "com.eletac.tronwallet.block_explorer_updater.accounts_updated";
    public static final String TOKENS_UPDATED = "com.eletac.tronwallet.block_explorer_updater.tokens_updated";

    private static Context mContext;

    private static Handler mTaskHandler;
    private static BlockchainUpdaterRunnable mBlockchainUpdaterRunnable;
    private static NodesUpdaterRunnable mNodesUpdaterRunnable;
    private static WitnessesUpdaterRunnable mWitnessesUpdaterRunnable;
    private static TokensUpdaterRunnable mTokensUpdaterRunnable;
    private static AccountsUpdaterRunnable mAccountsUpdaterRunnable;

    private static Map<UpdateTask, Long> mIntervals;

    private static Map<UpdateTask, Boolean> mRunning;
    private static Map<UpdateTask, Boolean> mSingleShot;

    private static ExecutorService mExecutorService;


    public static void init(Context context, Map<UpdateTask, Long> intervals) {
        if(mContext == null) {
            mContext = context;
            mIntervals = intervals;
            mRunning = new HashMap<>();
            mRunning.put(UpdateTask.Blockchain, false);
            mRunning.put(UpdateTask.Nodes, false);
            mRunning.put(UpdateTask.Witnesses, false);
            mRunning.put(UpdateTask.Tokens, false);
            mRunning.put(UpdateTask.Accounts, false);

            mSingleShot = new HashMap<>();
            mSingleShot.put(UpdateTask.Blockchain, false);
            mSingleShot.put(UpdateTask.Nodes, false);
            mSingleShot.put(UpdateTask.Witnesses, false);
            mSingleShot.put(UpdateTask.Tokens, false);
            mSingleShot.put(UpdateTask.Accounts, false);


            mTaskHandler = new Handler(Looper.getMainLooper());
            mBlockchainUpdaterRunnable = new BlockchainUpdaterRunnable();
            mNodesUpdaterRunnable = new NodesUpdaterRunnable();
            mWitnessesUpdaterRunnable = new WitnessesUpdaterRunnable();
            mTokensUpdaterRunnable = new TokensUpdaterRunnable();
            mAccountsUpdaterRunnable = new AccountsUpdaterRunnable();

            mExecutorService = Executors.newFixedThreadPool(2);
        }
    }

    public static void start(UpdateTask task) {
        stop(task);
        mRunning.put(task, true);
        mTaskHandler.post(getRunnableOfTask(task));
    }

    public static void stop(UpdateTask task) {
        mRunning.put(task, false);
        mTaskHandler.removeCallbacks(getRunnableOfTask(task));
    }

    public static void stopAll() {
        mRunning.put(UpdateTask.Blockchain, false);
        mRunning.put(UpdateTask.Nodes, false);
        mRunning.put(UpdateTask.Witnesses, false);
        mRunning.put(UpdateTask.Tokens, false);
        mRunning.put(UpdateTask.Accounts, false);
        mTaskHandler.removeCallbacks(null);
    }

    public static void singleShot(UpdateTask task, boolean now) {
        mSingleShot.put(task, true);
        if(now)
            mTaskHandler.post(getRunnableOfTask(task));
        else
            mTaskHandler.postDelayed(getRunnableOfTask(task), mIntervals.containsKey(task) ? mIntervals.get(task) : 0);
    }

    private static Runnable getRunnableOfTask(UpdateTask task) {
        switch (task) {
            case Blockchain:
                return mBlockchainUpdaterRunnable;
            case Nodes:
                return mNodesUpdaterRunnable;
            case Witnesses:
                return mWitnessesUpdaterRunnable;
            case Tokens:
                return mTokensUpdaterRunnable;
            case Accounts:
                return mAccountsUpdaterRunnable;
            default:
                return null;
        }
    }

    private static class BlockchainUpdaterRunnable implements Runnable {

        @Override
        public void run() {
            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                    @Override
                    public void doOnBackground() {
                        if (mContext != null) {
                        }

                        AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                            @Override
                            public void doInUIThread() {

                                Intent updatedIntent = new Intent(BLOCKCHAIN_UPDATED);
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(updatedIntent);

                                if(mRunning.get(UpdateTask.Blockchain)) {
                                    mTaskHandler.removeCallbacks(getRunnableOfTask(UpdateTask.Blockchain)); // remove multiple callbacks
                                    mTaskHandler.postDelayed(mBlockchainUpdaterRunnable, mIntervals.get(UpdateTask.Blockchain));
                                }
                                mSingleShot.put(UpdateTask.Blockchain, false);
                            }
                        });
                }
            }, mExecutorService);
        }
    }

    private static class NodesUpdaterRunnable implements Runnable {

        @Override
        public void run() {
            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {
                    if(mContext != null) {
                    }

                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {

                            Intent updatedIntent = new Intent(NODES_UPDATED);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(updatedIntent);

                            if(mRunning.get(UpdateTask.Nodes)) {
                                mTaskHandler.removeCallbacks(getRunnableOfTask(UpdateTask.Nodes)); // remove multiple callbacks
                                mTaskHandler.postDelayed(mNodesUpdaterRunnable, mIntervals.get(UpdateTask.Nodes));
                            }
                            mSingleShot.put(UpdateTask.Nodes, false);
                        }
                    });
                }
            }, mExecutorService);
        }
    }

    private static class WitnessesUpdaterRunnable implements Runnable {

        @Override
        public void run() {
            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {
                    if(mContext != null) {
                    }

                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {

                            Intent updatedIntent = new Intent(WITNESSES_UPDATED);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(updatedIntent);

                            if(mRunning.get(UpdateTask.Witnesses)) {
                                mTaskHandler.removeCallbacks(getRunnableOfTask(UpdateTask.Witnesses)); // remove multiple callbacks
                                mTaskHandler.postDelayed(mWitnessesUpdaterRunnable, mIntervals.get(UpdateTask.Witnesses));
                            }
                            mSingleShot.put(UpdateTask.Witnesses, false);
                        }
                    });
                }
            }, mExecutorService);
        }
    }

    private static class TokensUpdaterRunnable implements Runnable {

        @Override
        public void run() {
            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {
                    if(mContext != null) {
                    }

                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {

                            Intent updatedIntent = new Intent(TOKENS_UPDATED);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(updatedIntent);

                            if(mRunning.get(UpdateTask.Tokens)) {
                                mTaskHandler.removeCallbacks(getRunnableOfTask(UpdateTask.Tokens)); // remove multiple callbacks
                                mTaskHandler.postDelayed(mTokensUpdaterRunnable, mIntervals.get(UpdateTask.Tokens));
                            }
                            mSingleShot.put(UpdateTask.Tokens, false);
                        }
                    });
                }
            }, mExecutorService);
        }
    }

    private static class AccountsUpdaterRunnable implements Runnable {

        @Override
        public void run() {
            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {
                    if(mContext != null) {
                        // Load accounts
                        /*try {
                            GrpcAPI.AccountList result = WalletManager.listAccounts();
                            if(result != null) {
                                mAccounts.clear();
                                mAccounts.addAll(result.getAccountsList());
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }

                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {

                            Intent updatedIntent = new Intent(ACCOUNTS_UPDATED);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(updatedIntent);

                            if(mRunning.get(UpdateTask.Accounts)) {
                                mTaskHandler.removeCallbacks(getRunnableOfTask(UpdateTask.Accounts)); // remove multiple callbacks
                                mTaskHandler.postDelayed(mAccountsUpdaterRunnable, mIntervals.get(UpdateTask.Accounts));
                            }
                            mSingleShot.put(UpdateTask.Accounts, false);
                        }
                    });
                }
            }, mExecutorService);
        }
    }


    public static boolean isRunning(UpdateTask task) {
        return mRunning.get(task);
    }
    public static boolean isSingleShotting(UpdateTask task) {
        return mSingleShot.get(task);
    }
}
