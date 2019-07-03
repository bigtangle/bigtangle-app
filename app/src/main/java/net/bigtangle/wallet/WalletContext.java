package net.bigtangle.wallet;

import android.content.Context;

import net.bigtangle.core.NetworkParameters;
import net.bigtangle.kits.WalletAppKit;
import net.bigtangle.params.MainNetParams;

import java.io.File;

public class WalletContext {

    private WalletAppKit walletAppKit;

    public static NetworkParameters networkParameters = MainNetParams.get();

    public void initWalletData(String directory, String filename) {
        walletAppKit = new WalletAppKit(networkParameters, new File(directory), filename);
    }

    public void createWallet() {
    }

    public static WalletContext instance = new WalletContext();

    public static WalletContext getInstance() {
        return instance;
    }

}
