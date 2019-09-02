package net.bigtangle.wallet.core.utils;

import android.util.Log;

import net.bigtangle.kits.WalletUtil;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WalletFileUtils {

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
}
