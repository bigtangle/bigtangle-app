package net.bigtangle.wallet.core.utils;

import android.content.Context;
import android.util.Log;

import net.bigtangle.kits.WalletUtil;
import net.bigtangle.wallet.activity.wallet.dialog.WalletDownfileDialog;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WalletFileUtils {

    public static void createWalletDB(Context context) throws IOException {
        byte[] b = WalletUtil.createWallet(WalletContextHolder.networkParameters);
        CommonUtil.deleteDB(context);
        InputStream stream =new ByteArrayInputStream(b);
        CommonUtil.saveDB("bigtangle", b, context);

        WalletContextHolder.loadWallet(stream);

    }

    public static void createWalletFile(String walletDirectory, String walletFilename) throws IOException {
        byte[] b = WalletUtil.createWallet(WalletContextHolder.networkParameters);
        File file = new File(walletDirectory + walletFilename);
        FileOutputStream fileInputStream = new FileOutputStream(file);
        fileInputStream.write(b);
        fileInputStream.flush();
        fileInputStream.close();
    }

    public static void createWalletFileAndLoad() {
        try {
            String walletDirectory = LocalStorageContext.get().readWalletDirectory();
            String walletFilename = LocalStorageContext.get().readWalletFilePrefix();
            createWalletFile(walletDirectory, walletFilename + ".wallet");
        } catch (Exception e) {
            Log.e(LogConstant.TAG, "createWalletFileAndLoad", e);
        }
    }

    public static void download(String username, String password, WalletDownfileDialog.OnWalletDownfileListenter listenter, Context context) {
        try {
            HttpService.downloadWalletFile(username, password, LocalStorageContext.get().readWalletDirectory() + "download.wallet", listenter, context);
        } catch (Exception e) {
            Log.e(LogConstant.TAG, "download", e);
            listenter.downloadFileStatus(false, e);
        }
    }
}
