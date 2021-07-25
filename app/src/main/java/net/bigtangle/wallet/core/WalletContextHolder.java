package net.bigtangle.wallet.core;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.crypto.KeyCrypterScrypt;
import net.bigtangle.kits.WalletAppKit;
import net.bigtangle.kits.WalletUtil;
import net.bigtangle.params.MainNetParams;
import net.bigtangle.params.TestParams;
import net.bigtangle.wallet.Wallet;

import org.apache.commons.lang3.StringUtils;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class WalletContextHolder {

    private WalletAppKit walletAppKit;

    public static NetworkParameters networkParameters =
          //  TestParams.get();
    MainNetParams.get();
    public static InputStream inputStream;
    public static Wallet wallet;
    private static String password;
    public static String username;
    public static String userpwd;

    public void reloadWalletFile(String directory, String filename) {
        walletAppKit = new WalletAppKit(networkParameters, new File(directory), filename);
        setupWalletData();
    }

    public static String getMBigtangle() {
        if (networkParameters instanceof TestParams)
            return "http://testm.bigtangle.xyz";
        else return "https://m.bigtangle.xyz";
    }

    public static KeyParameter getAesKey() {
        KeyParameter aesKey = null;
        if (StringUtils.isBlank(password)) {
            return aesKey;
        }
        final KeyCrypterScrypt keyCrypter = (KeyCrypterScrypt) wallet.getKeyCrypter();
        if (keyCrypter == null) {
            return aesKey;
        }
        if (!"".equals(password.trim())) {
            aesKey = keyCrypter.deriveKey(password);
        }
        return aesKey;
    }

    public static boolean checkWalletPassword(String password) {
        KeyParameter aesKey = null;
        final KeyCrypterScrypt keyCrypter = (KeyCrypterScrypt) wallet.getKeyCrypter();
        if (!"".equals(password.trim())) {
            aesKey = keyCrypter.deriveKey(password);
        }
        return wallet.checkAESKey(aesKey);
    }


    public static boolean checkWalletHavePassword() {
        try {
            final KeyCrypterScrypt keyCrypter = (KeyCrypterScrypt) wallet.getKeyCrypter();
            return keyCrypter != null;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean saveAndCheckPassword(String newpassword) {
        if (checkWalletPassword(newpassword)) {
            password = newpassword;
            return true;
        }
        return false;
    }


    public static List<ECKey> walletKeys() {
        return walletKeys0();
    }

    private void setupWalletData() {
        this.wallet();
    }


    private static WalletContextHolder instance = new WalletContextHolder();

    public static  WalletContextHolder get() {
        return instance;
    }


    public Wallet wallet() {
        if (walletAppKit == null) {
            return null;
        }
        return this.walletAppKit.wallet();
    }


    public static Wallet loadWallet(InputStream is) {
        try {
            wallet = WalletUtil.loadWallet(false, is, networkParameters);
            return wallet;
        } catch (Exception e) {
            return null;
        }

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

    public static String getCurrentPassword() {
        return password;
    }

    public static void savePasswordToLocal(String newpassword) {
        password = newpassword;
    }

    public static List<ECKey> walletKeys0() {
        List<ECKey> issuedKeys = null;
        if (wallet.isEncrypted()) {
            // 加密之后 读取ECKey 需要 aesKey
            issuedKeys = wallet.walletKeys(getAesKey());
        } else {
            issuedKeys = wallet.walletKeys();
        }
        return issuedKeys;
    }
}
