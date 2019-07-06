package net.bigtangle.wallet.core;

import net.bigtangle.core.NetworkParameters;
import net.bigtangle.kits.WalletAppKit;
import net.bigtangle.params.MainNetParams;
import net.bigtangle.wallet.Wallet;

import java.io.File;

public class WalletContextHolder {

    private WalletAppKit walletAppKit;

    public static NetworkParameters networkParameters = MainNetParams.get();

    public void initWalletData(String directory, String filename) {
        if (walletAppKit == null) {
            walletAppKit = new WalletAppKit(networkParameters, new File(directory), filename);
        }
        setupWalletData();
    }

    private void setupWalletData() {
        this.wallet();
    }

    private static WalletContextHolder instance = new WalletContextHolder();

    public static final WalletContextHolder get() {
        return instance;
    }

    public Wallet wallet() {
        if (walletAppKit == null ) {
            return null;
        }
        Wallet wallet = this.walletAppKit.wallet();
        return wallet;
    }
}
