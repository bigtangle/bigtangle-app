package net.bigtangle.wallet.core;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.crypto.KeyCrypterScrypt;
import net.bigtangle.kits.WalletAppKit;
import net.bigtangle.params.MainNetParams;
import net.bigtangle.params.TestParams;
import net.bigtangle.wallet.Wallet;

import org.apache.commons.lang3.StringUtils;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.File;
import java.util.List;

public class WalletContextHolder {

    private WalletAppKit walletAppKit;

    public static NetworkParameters networkParameters = TestParams.get();//MainNetParams.get();


    private String password;

    public void reloadWalletFile(String directory, String filename) {
        walletAppKit = new WalletAppKit(networkParameters, new File(directory), filename);
        setupWalletData();
    }

    public static String getMBigtangle() {
        if (networkParameters instanceof TestParams)
            return "https://testm.bigtangle.xyz";
        else return "https://m.bigtangle.xyz";
    }

    public KeyParameter getAesKey() {
        KeyParameter aesKey = null;
        if (StringUtils.isBlank(this.password)) {
            return aesKey;
        }
        final KeyCrypterScrypt keyCrypter = (KeyCrypterScrypt) WalletContextHolder.get().wallet().getKeyCrypter();
        if (keyCrypter == null) {
            return aesKey;
        }
        if (!"".equals(password.trim())) {
            aesKey = keyCrypter.deriveKey(password);
        }
        return aesKey;
    }

    public boolean checkWalletPassword(String password) {
        KeyParameter aesKey = null;
        final KeyCrypterScrypt keyCrypter = (KeyCrypterScrypt) WalletContextHolder.get().wallet().getKeyCrypter();
        if (!"".equals(password.trim())) {
            aesKey = keyCrypter.deriveKey(password);
        }
        return wallet().checkAESKey(aesKey);
    }

    public boolean checkWalletHavePassword() {
        final KeyCrypterScrypt keyCrypter = (KeyCrypterScrypt) WalletContextHolder.get().wallet().getKeyCrypter();
        return keyCrypter != null;
    }

    public boolean saveAndCheckPassword(String password) {
        if (checkWalletPassword(password)) {
            this.password = password;
            return true;
        }
        return false;
    }

    public List<ECKey> walletKeys() {
        return walletKeys0();
    }

    private void setupWalletData() {
        this.wallet();
    }

    private static WalletContextHolder instance = new WalletContextHolder();

    public static final WalletContextHolder get() {
        return instance;
    }

    public Wallet wallet() {
        if (walletAppKit == null) {
            return null;
        }
        Wallet wallet = this.walletAppKit.wallet();
        return wallet;
    }

    public boolean checkWalletExists() {
        String walletDirectory = LocalStorageContext.get().readWalletDirectory();
        String walletFilename = LocalStorageContext.get().readWalletFilePrefix();

        File file = new File(walletDirectory + walletFilename + ".wallet");
        return file.exists();
    }

    public void initData() {
        String walletDirectory = LocalStorageContext.get().readWalletDirectory();
        String walletFilename = LocalStorageContext.get().readWalletFilePrefix();
        WalletContextHolder.get().reloadWalletFile(walletDirectory, walletFilename);
    }

    public String getCurrentPassword() {
        return this.password;
    }

    public void savePasswordToLocal(String password) {
        this.password = password;
    }

    public List<ECKey> walletKeys0() {
        List<ECKey> issuedKeys = null;
        if (wallet().isEncrypted()) {
            // 加密之后 读取ECKey 需要 aesKey
            issuedKeys = wallet().walletKeys(getAesKey());
        } else {
            issuedKeys = wallet().walletKeys();
        }
        return issuedKeys;
    }
}
